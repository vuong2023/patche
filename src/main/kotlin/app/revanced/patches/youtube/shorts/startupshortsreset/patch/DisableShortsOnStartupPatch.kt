package app.revanced.patches.youtube.shorts.startupshortsreset.patch

import app.revanced.extensions.exception
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.shorts.startupshortsreset.fingerprints.UserWasInShortsFingerprint
import app.revanced.patches.youtube.utils.annotations.YouTubeCompatibility
import app.revanced.patches.youtube.utils.settings.resource.patch.SettingsPatch
import app.revanced.util.integrations.Constants.SHORTS
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@Name("Disable Shorts on startup")
@Description("Disables playing YouTube Shorts when launching YouTube.")
@DependsOn([SettingsPatch::class])
@YouTubeCompatibility

class DisableShortsOnStartupPatch : BytecodePatch(
    listOf(UserWasInShortsFingerprint)
) {
    override fun execute(context: BytecodeContext) {

        UserWasInShortsFingerprint.result?.let {
            it.mutableMethod.apply {
                val targetIndex = it.scanResult.patternScanResult!!.endIndex
                val register = getInstruction<OneRegisterInstruction>(targetIndex).registerA + 2
                addInstructionsWithLabels(
                    targetIndex + 1,
                    """
                        invoke-static { }, $SHORTS->disableStartupShortsPlayer()Z
                        move-result v$register
                        if-eqz v$register, :show_startup_shorts_player
                        return-void
                        """,
                    ExternalLabel("show_startup_shorts_player", getInstruction(targetIndex + 1))
                )
            }
        } ?: throw UserWasInShortsFingerprint.exception

        /**
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "PREFERENCE: SHORTS_SETTINGS",
                "SETTINGS: SHORTS_PLAYER_PARENT",
                "SETTINGS: DISABLE_STARTUP_SHORTS_PLAYER"
            )
        )

        SettingsPatch.updatePatchStatus("disable-startup-shorts-player")
    }
}
