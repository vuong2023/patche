package app.revanced.patches.music.utils.settings.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name

import app.revanced.patcher.data.ResourceContext

import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.music.utils.annotations.MusicCompatibility
import app.revanced.patches.music.utils.fix.decoding.patch.DecodingPatch
import app.revanced.patches.music.utils.settings.bytecode.patch.SettingsBytecodePatch
import app.revanced.patches.shared.patch.settings.AbstractSettingsResourcePatch
import app.revanced.util.enum.CategoryType
import app.revanced.util.resources.IconHelper
import app.revanced.util.resources.IconHelper.copyFiles
import app.revanced.util.resources.IconHelper.makeDirectoryAndCopyFiles
import app.revanced.util.resources.MusicResourceHelper.addMusicPreference
import app.revanced.util.resources.MusicResourceHelper.addMusicPreferenceCategory
import app.revanced.util.resources.MusicResourceHelper.addMusicPreferenceWithIntent
import app.revanced.util.resources.MusicResourceHelper.addReVancedMusicPreference
import app.revanced.util.resources.MusicResourceHelper.sortMusicPreferenceCategory
import app.revanced.util.resources.ResourceUtils
import app.revanced.util.resources.ResourceUtils.copyResources
import org.w3c.dom.Element
import java.io.File
import java.nio.file.Paths

@Patch
@Name("Settings")
@Description("Adds settings for ReVanced to YouTube Music.")
@DependsOn(
    [
        DecodingPatch::class,
        SettingsBytecodePatch::class
    ]
)
@MusicCompatibility

class SettingsPatch : AbstractSettingsResourcePatch(
    "music/settings",
    "music/settings/host",
    false
) {
    override fun execute(context: ResourceContext) {
        super.execute(context)
        contexts = context

        /**
         * create directory for the untranslated language resources
         */
        context["res/values-v21"].mkdirs()

        arrayOf(
            ResourceUtils.ResourceGroup(
                "values-v21",
                "strings.xml"
            )
        ).forEach { resourceGroup ->
            context.copyResources("music/settings", resourceGroup)
        }

        /**
         * Copy colors
         */
        context.xmlEditor["res/values/colors.xml"].use { editor ->
            val resourcesNode = editor.file.getElementsByTagName("resources").item(0) as Element

            for (i in 0 until resourcesNode.childNodes.length) {
                val node = resourcesNode.childNodes.item(i) as? Element ?: continue

                node.textContent = when (node.getAttribute("name")) {
                    "material_deep_teal_500" -> "@android:color/white"

                    else -> continue
                }
            }
        }

        context.addReVancedMusicPreference()

        /**
         * If a custom branding icon path exists, merge it
         */
        val iconPath = "branding-music"
        val targetDirectory = Paths.get("").toAbsolutePath().toString() + "/$iconPath"

        if (File(targetDirectory).exists()) {
            fun copyResources(resourceGroups: List<ResourceUtils.ResourceGroup>) {
                try {
                    context.copyFiles(resourceGroups, iconPath)
                } catch (_: Exception) {
                    context.makeDirectoryAndCopyFiles(resourceGroups, iconPath)
                }
            }

            val iconResourceFileNames =
                IconHelper.YOUTUBE_MUSIC_LAUNCHER_ICON_ARRAY
                    .map { "$it.png" }
                    .toTypedArray()

            fun createGroup(directory: String) = ResourceUtils.ResourceGroup(
                directory, *iconResourceFileNames
            )

            arrayOf("xxxhdpi", "xxhdpi", "xhdpi", "hdpi", "mdpi")
                .map { "mipmap-$it" }
                .map(::createGroup)
                .let(::copyResources)
        }
    }

    companion object {
        lateinit var contexts: ResourceContext

        internal fun addMusicPreference(
            category: CategoryType,
            key: String,
            defaultValue: String
        ) {
            val categoryValue = category.value
            contexts.addMusicPreferenceCategory(categoryValue)
            contexts.addMusicPreference(categoryValue, key, defaultValue)
            contexts.sortMusicPreferenceCategory(categoryValue)
        }

        internal fun addMusicPreferenceWithIntent(
            category: CategoryType,
            key: String,
            dependencyKey: String

        ) {
            val categoryValue = category.value
            contexts.addMusicPreferenceCategory(categoryValue)
            contexts.addMusicPreferenceWithIntent(categoryValue, key, dependencyKey)
            contexts.sortMusicPreferenceCategory(categoryValue)
        }
    }
}
