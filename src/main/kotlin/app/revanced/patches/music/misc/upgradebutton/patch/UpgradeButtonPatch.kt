package app.revanced.patches.music.misc.upgradebutton.patch

import app.revanced.extensions.exception
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.music.misc.upgradebutton.fingerprints.NotifierShelfFingerprint
import app.revanced.patches.music.misc.upgradebutton.fingerprints.PivotBarConstructorFingerprint
import app.revanced.patches.music.utils.annotations.MusicCompatibility
import app.revanced.patches.music.utils.fix.decoding.patch.DecodingPatch
import app.revanced.patches.music.utils.integrations.patch.IntegrationsPatch
import app.revanced.patches.music.utils.resourceid.patch.SharedResourceIdPatch
import app.revanced.util.integrations.Constants.MUSIC_LAYOUT
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction

@Patch
@Name("Hide upgrade button")
@Description("Hides upgrade button from navigation bar and hide upgrade banner from homepage.")
@DependsOn(
    [
        DecodingPatch::class,
        IntegrationsPatch::class,
        SharedResourceIdPatch::class
    ]
)
@MusicCompatibility

class UpgradeButtonPatch : BytecodePatch(
    listOf(
        PivotBarConstructorFingerprint,
        NotifierShelfFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        PivotBarConstructorFingerprint.result?.let {
            it.mutableMethod.apply {
                val targetIndex = it.scanResult.patternScanResult!!.startIndex
                val targetRegister = getInstruction<TwoRegisterInstruction>(targetIndex).registerA

                addInstruction(
                    targetIndex,
                    "invoke-static {v$targetRegister}, $MUSIC_LAYOUT->hideUpgradeButton(Ljava/util/List;)V"
                )
            }
        } ?: throw PivotBarConstructorFingerprint.exception

        NotifierShelfFingerprint.result?.let {
            it.mutableMethod.apply {
                val targetIndex = it.scanResult.patternScanResult!!.endIndex
                val targetRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA
                addInstruction(
                    targetIndex + 1,
                    "invoke-static {v$targetRegister}, Lapp/revanced/music/utils/ReVancedUtils;->hideViewByLayoutParams(Landroid/view/View;)V"
                )
            }
        } ?: throw NotifierShelfFingerprint.exception
    }
}
