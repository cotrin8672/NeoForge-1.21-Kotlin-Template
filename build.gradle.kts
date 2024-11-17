import org.jetbrains.kotlin.gradle.utils.extendsFrom

plugins {
    alias(libs.plugins.neoforgeGradle)
    alias(libs.plugins.kotlin)
    idea
}

tasks.named<Wrapper>("wrapper").configure {
    distributionType = Wrapper.DistributionType.BIN
}

val modId: String by project
val modVersion: String by project
val modGroupId: String by project
val minecraftVersion: String by project
val minecraftVersionRange: String by project
val neoVersion: String by project
val neoVersionRange: String by project
val loaderVersion: String by project
val loaderVersionRange: String by project
val modName: String by project
val modLicense: String by project
val modAuthors: String by project
val modDescription: String by project

version = modVersion
group = modGroupId

repositories {
    mavenLocal()
    maven {
        name = "Kotlin For Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        content { includeGroup("thedarkcolour") }
    }
}

base {
    archivesName = modId
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

runs {
    configureEach {
        systemProperty("forge.logging.markers", "REGISTRIES")
        systemProperty("forge.logging.console.level", "debug")

        modSource(project.sourceSets.main.get())
    }

    create("client") {
        systemProperty("forge.enabledGameTestNamespaces", modId)
    }

    create("server") {
        systemProperty("forge.enabledGameTestNamespaces", modId)
        programArgument("--nogui")
    }

    create("data") {
        programArguments.addAll(
            "--mod",
            modId,
            "--all",
            "--output",
            file("src/generated/resources/").absolutePath,
            "--existing",
            file("src/main/resources/").absolutePath
        )
    }
}

sourceSets.main.get().resources { srcDir("src/generated/resources") }

configurations {
    runtimeClasspath.extendsFrom(localRuntime)
}

dependencies {
    implementation(libs.neoforge)
    implementation(libs.kotlinforforge)
}

tasks.withType<ProcessResources>().configureEach {
    val replaceProperties = mapOf(
        "minecraftVersion" to minecraftVersion,
        "minecraftVersionRange" to minecraftVersionRange,
        "neoVersionRange" to neoVersionRange,
        "loaderVersionRange" to loaderVersionRange,
        "modId" to modId,
        "modName" to modName,
        "modLicense" to modLicense,
        "modVersion" to modVersion,
        "modAuthors" to modAuthors,
        "modDescription" to modDescription
    )
    inputs.properties(replaceProperties)

    filesMatching(listOf("META-INF/neoforge.mods.toml")) {
        expand(replaceProperties)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
