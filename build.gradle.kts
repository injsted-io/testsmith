import org.gradle.api.file.DuplicatesStrategy

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.10"
    id("org.jetbrains.intellij") version "1.17.3"
}

group = "com.testsmith"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
}

intellij {
    version.set("2024.3.5")
    type.set("IU")
    updateSinceUntilBuild.set(false)
}

sourceSets {
    main {
        java.srcDirs("src/main/kotlin")
        resources.srcDirs("src/main/resources")
    }
}

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE // ✅ This is the fix
    }

    patchPluginXml {
        sinceBuild.set("243")
    }
}
