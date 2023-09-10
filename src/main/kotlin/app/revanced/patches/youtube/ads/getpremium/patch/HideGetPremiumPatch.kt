package app.revanced.patches.youtube.ads.getpremium.patch

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.ads.getpremium.fingerprints.CompactYpcOfferModuleViewFingerprint
import app.revanced.util.integrations.Constants.PATCHES_PATH
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction

class HideGetPremiumPatch : BytecodePatch(
    listOf(CompactYpcOfferModuleViewFingerprint)
) {
    override fun execute(context: BytecodeContext) {

        CompactYpcOfferModuleViewFingerprint.result?.let {
            it.mutableMethod.apply {
                val startIndex = it.scanResult.patternScanResult!!.startIndex
                val measuredWidthRegister =
                    getInstruction<TwoRegisterInstruction>(startIndex).registerA
                val measuredHeightInstruction =
                    getInstruction<TwoRegisterInstruction>(startIndex + 1)
                val measuredHeightRegister = measuredHeightInstruction.registerA
                val tempRegister = measuredHeightInstruction.registerB

                addInstructionsWithLabels(
                    startIndex + 2, """
                        invoke-static {}, $PATCHES_PATH/ads/AdsFilter;->hideGetPremium()Z
                        move-result v$tempRegister
                        if-eqz v$tempRegister, :show
                        const/4 v$measuredWidthRegister, 0x0
                        const/4 v$measuredHeightRegister, 0x0
                        """, ExternalLabel("show", getInstruction(startIndex + 2))
                )
            }
        } ?: throw CompactYpcOfferModuleViewFingerprint.exception
    }
}
