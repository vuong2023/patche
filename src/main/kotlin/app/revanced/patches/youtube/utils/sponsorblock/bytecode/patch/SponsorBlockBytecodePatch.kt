package app.revanced.patches.youtube.utils.sponsorblock.bytecode.patch

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.utils.fingerprints.SeekbarFingerprint
import app.revanced.patches.youtube.utils.fingerprints.SeekbarOnDrawFingerprint
import app.revanced.patches.youtube.utils.fingerprints.TotalTimeFingerprint
import app.revanced.patches.youtube.utils.fingerprints.YouTubeControlsOverlayFingerprint
import app.revanced.patches.youtube.utils.overridespeed.patch.OverrideSpeedHookPatch
import app.revanced.patches.youtube.utils.playercontrols.patch.PlayerControlsPatch
import app.revanced.patches.youtube.utils.resourceid.patch.SharedResourceIdPatch
import app.revanced.patches.youtube.utils.resourceid.patch.SharedResourceIdPatch.Companion.InsetOverlayViewLayout
import app.revanced.patches.youtube.utils.resourceid.patch.SharedResourceIdPatch.Companion.TotalTime
import app.revanced.patches.youtube.utils.sponsorblock.bytecode.fingerprints.RectangleFieldInvalidatorFingerprint
import app.revanced.patches.youtube.utils.sponsorblock.bytecode.fingerprints.SegmentPlaybackControllerFingerprint
import app.revanced.patches.youtube.utils.videoid.general.patch.VideoIdPatch
import app.revanced.patches.youtube.utils.videoid.withoutshorts.patch.VideoIdWithoutShortsPatch
import app.revanced.util.bytecode.BytecodeHelper.injectInit
import app.revanced.util.bytecode.getWideLiteralIndex
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.BuilderInstruction
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction3rc
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@DependsOn(
    [
        OverrideSpeedHookPatch::class,
        PlayerControlsPatch::class,
        SharedResourceIdPatch::class,
        VideoIdPatch::class,
        VideoIdWithoutShortsPatch::class
    ]
)
class SponsorBlockBytecodePatch : BytecodePatch(
    listOf(
        SeekbarFingerprint,
        SegmentPlaybackControllerFingerprint,
        TotalTimeFingerprint,
        YouTubeControlsOverlayFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {

        /**
         * Hook the video time methods
         */
        VideoIdPatch.apply {
            videoTimeHook(
                INTEGRATIONS_PLAYER_CONTROLLER_CLASS_DESCRIPTOR,
                "setVideoTime"
            )
            onCreateHook(
                INTEGRATIONS_PLAYER_CONTROLLER_CLASS_DESCRIPTOR,
                "initialize"
            )
        }


        /**
         * Seekbar drawing
         */
        SeekbarFingerprint.result?.mutableClass?.let { mutableClass ->
            insertMethod = SeekbarOnDrawFingerprint.also {
                it.resolve(
                    context,
                    mutableClass
                )
            }.result?.mutableMethod
                ?: throw SeekbarOnDrawFingerprint.exception
            insertInstructions = insertMethod.implementation!!.instructions
        } ?: throw SeekbarFingerprint.exception


        /**
         * Get left and right of seekbar rectangle
         */
        val moveRectangleToRegisterIndex = insertInstructions.indexOfFirst {
            it.opcode == Opcode.MOVE_OBJECT_FROM16
        }

        insertMethod.addInstruction(
            moveRectangleToRegisterIndex + 1,
            "invoke-static/range {p0 .. p0}, " +
                    "$INTEGRATIONS_PLAYER_CONTROLLER_CLASS_DESCRIPTOR->setSponsorBarRect(Ljava/lang/Object;)V"
        )

        for ((index, instruction) in insertInstructions.withIndex()) {
            if (instruction.opcode != Opcode.INVOKE_STATIC) continue

            val invokeInstruction = insertMethod.getInstruction<Instruction35c>(index)
            if ((invokeInstruction.reference as MethodReference).name != "round") continue

            val insertIndex = index + 2

            insertMethod.addInstruction(
                insertIndex,
                "invoke-static {v${invokeInstruction.registerC}}, " +
                        "$INTEGRATIONS_PLAYER_CONTROLLER_CLASS_DESCRIPTOR->setSponsorBarThickness(I)V"
            )
            break
        }


        /**
         * Draw segment
         */
        for ((index, instruction) in insertInstructions.withIndex()) {
            if (instruction.opcode != Opcode.INVOKE_VIRTUAL_RANGE) continue

            val invokeInstruction = instruction as BuilderInstruction3rc
            if ((invokeInstruction.reference as MethodReference).name != "restore") continue

            val drawSegmentInstructionInsertIndex = index - 1

            val (canvasInstance, centerY) =
                insertMethod.getInstruction<FiveRegisterInstruction>(
                    drawSegmentInstructionInsertIndex
                ).let { it.registerC to it.registerE }

            insertMethod.addInstruction(
                drawSegmentInstructionInsertIndex,
                "invoke-static {v$canvasInstance, v$centerY}, $INTEGRATIONS_PLAYER_CONTROLLER_CLASS_DESCRIPTOR->drawSponsorTimeBars(Landroid/graphics/Canvas;F)V"
            )
            break
        }


        /**
         * Voting & Shield button
         */
        arrayOf("CreateSegmentButtonController", "VotingButtonController").forEach {
            PlayerControlsPatch.initializeSB("$INTEGRATIONS_BUTTON_CLASS_DESCRIPTOR/ui/$it;")
            PlayerControlsPatch.injectVisibility("$INTEGRATIONS_BUTTON_CLASS_DESCRIPTOR/ui/$it;")
        }


        /**
         * Append the new time to the player layout
         */
        TotalTimeFingerprint.result?.let {
            it.mutableMethod.apply {
                val targetIndex = getWideLiteralIndex(TotalTime) + 2
                val targetRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

                addInstructions(
                    targetIndex + 1, """
                        invoke-static {v$targetRegister}, $INTEGRATIONS_PLAYER_CONTROLLER_CLASS_DESCRIPTOR->appendTimeWithoutSegments(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object v$targetRegister
                        """
                )
            }
        } ?: throw TotalTimeFingerprint.exception


        /**
         * Initialize the SponsorBlock view
         */
        YouTubeControlsOverlayFingerprint.result?.let {
            it.mutableMethod.apply {
                val targetIndex = getWideLiteralIndex(InsetOverlayViewLayout) + 3
                val targetRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

                addInstruction(
                    targetIndex + 1,
                    "invoke-static {v$targetRegister}, $INTEGRATIONS_BUTTON_CLASS_DESCRIPTOR/ui/SponsorBlockViewController;->initialize(Landroid/view/ViewGroup;)V"
                )
            }
        } ?: throw YouTubeControlsOverlayFingerprint.exception


        /**
         * Replace strings
         */
        SeekbarFingerprint.result?.mutableClass?.let { mutableClass ->
            RectangleFieldInvalidatorFingerprint.also {
                it.resolve(
                    context,
                    mutableClass
                )
            }.result?.let {
                it.mutableMethod.apply {
                    val rectangleReference =
                        getInstruction<ReferenceInstruction>(implementation!!.instructions.count() - 3).reference
                    val rectangleFieldName = (rectangleReference as FieldReference).name

                    SegmentPlaybackControllerFingerprint.result?.let { result ->
                        result.mutableMethod.apply {
                            for ((index, instruction) in implementation!!.instructions.withIndex()) {
                                if (instruction.opcode != Opcode.CONST_STRING) continue

                                val register =
                                    getInstruction<OneRegisterInstruction>(index).registerA

                                replaceInstruction(
                                    index,
                                    "const-string v$register, \"$rectangleFieldName\""
                                )
                                break
                            }
                        }
                    } ?: throw SegmentPlaybackControllerFingerprint.exception
                }
            } ?: throw RectangleFieldInvalidatorFingerprint.exception
        } ?: throw SeekbarFingerprint.exception


        /**
         * Inject VideoIdPatch
         */
        VideoIdWithoutShortsPatch.injectCall("$INTEGRATIONS_PLAYER_CONTROLLER_CLASS_DESCRIPTOR->setCurrentVideoId(Ljava/lang/String;)V")

        context.injectInit("FirstRun", "initializationSB", true)

    }

    internal companion object {
        const val INTEGRATIONS_BUTTON_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/sponsorblock"

        const val INTEGRATIONS_PLAYER_CONTROLLER_CLASS_DESCRIPTOR =
            "$INTEGRATIONS_BUTTON_CLASS_DESCRIPTOR/SegmentPlaybackController;"

        lateinit var insertMethod: MutableMethod
        lateinit var insertInstructions: List<BuilderInstruction>
    }
}