plugins {
    kotlin("jvm") version "1.9.0"
}

group = "app.revanced"

val githubUsername: String = project.findProperty("gpr.user") as? String ?: System.getenv("GITHUB_ACTOR")
val githubPassword: String = project.findProperty("gpr.key") as? String ?: System.getenv("GITHUB_TOKEN")

repositories {
    google()
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://maven.pkg.github.com/revanced/revanced-patcher")
        credentials {
            username = githubUsername
            password = githubPassword
        }
    }
}

dependencies {
    implementation("app.revanced:revanced-patcher:14.2.1")
    implementation("com.android.tools.smali:smali:3.0.3")
    implementation("com.android.tools.smali:smali-dexlib2:3.0.3")
    // Required because build fails without it.
    // TODO: Find a way to remove this dependency.
    implementation("com.google.guava:guava:32.1.2-jre")
    // Used in JsonGenerator
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks {
    register<DefaultTask>("generateBundle") {
        description = "Generate dex files from build and bundle them in the jar file"
        dependsOn(build)

        doLast {
            val androidHome = System.getenv("ANDROID_HOME") ?: throw GradleException("ANDROID_HOME not found")
            val d8 = "${androidHome}/build-tools/33.0.1/d8"
            val input = configurations.archives.get().allArtifacts.files.files.first().absolutePath
            val work = File("${buildDir}/libs")

            exec {
                workingDir = work
                commandLine = listOf(d8, input)
            }

            exec {
                workingDir = work
                commandLine = listOf("zip", "-u", input, "classes.dex")
            }
        }
    }
    register<JavaExec>("generateMeta") {
        description = "Generate metadata for this bundle"
        dependsOn(build)

        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("app.revanced.meta.PatchesFileGenerator")
    }
    // Dummy task to fix the Gradle semantic-release plugin.
    // Remove this if you forked it to support building only.
    // Tracking issue: https://github.com/KengoTODA/gradle-semantic-release-plugin/issues/435
    register<DefaultTask>("publish") {
        group = "publish"
        description = "Dummy task"
        dependsOn(named("generateBundle"), named("generateMeta"))
    }
}