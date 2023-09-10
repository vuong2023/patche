package app.revanced.patches.music.navigation.label.patch

import app.revanced.extensions.exception
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException

import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.music.utils.annotations.MusicCompatibility
import app.revanced.patches.music.utils.fingerprints.TabLayoutTextFingerprint
import app.revanced.patches.music.utils.resourceid.patch.SharedResourceIdPatch
import app.revanced.patches.music.utils.resourceid.patch.SharedResourceIdPatch.Companion.Text1
import app.revanced.patches.music.utils.settings.resource.patch.SettingsPatch
import app.revanced.patches.music.utils.settings.resource.patch.SettingsPatch.Companion.contexts
import app.revanced.util.bytecode.getWideLiteralIndex
import app.revanced.util.enum.CategoryType
import app.revanced.util.integrations.Constants.MUSIC_NAVIGATION
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction

@Patch
@Name("Hide navigation label")
@Description("Hide navigation bar labels.")
@DependsOn(
    [
        SettingsPatch::class,
        SharedResourceIdPatch::class
    ]
)
@MusicCompatibility

class NavigationLabelPatch : BytecodePatch(
    listOf(TabLayoutTextFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        TabLayoutTextFingerprint.result?.let {
            it.mutableMethod.apply {
                val targetIndex = getWideLiteralIndex(Text1) + 3
                val targetParameter = getInstruction<ReferenceInstruction>(targetIndex).reference
                val targetRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

                if (!targetParameter.toString().endsWith("Landroid/widget/TextView;"))
                    throw PatchException("Method signature parameter did not match: $targetParameter")

                addInstruction(
                    targetIndex + 1,
                    "invoke-static {v$targetRegister}, $MUSIC_NAVIGATION->hideNavigationLabel(Landroid/widget/TextView;)V"
                )
            }
        } ?: throw TabLayoutTextFingerprint.exception

        contexts.xmlEditor[RESOURCE_FILE_PATH].use { editor ->
            val document = editor.file

            with(document.getElementsByTagName("ImageView").item(0)) {
                if (attributes.getNamedItem(FLAG) != null) return@with

                document.createAttribute(FLAG)
                    .apply { value = "0.5" }
                    .let(attributes::setNamedItem)
            }
        }

        SettingsPatch.addMusicPreference(
            CategoryType.NAVIGATION,
            "revanced_hide_navigation_label",
            "false"
        )
    }

    private companion object {
        const val FLAG = "android:layout_weight"
        const val RESOURCE_FILE_PATH = "res/layout/image_with_text_tab.xml"
    }
}
