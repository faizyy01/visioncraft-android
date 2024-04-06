import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
    dependencies {
        classpath(libs.google.services)
    }
}
plugins {
    id("com.android.application") apply false
    id("com.android.library") apply false
    kotlin("android") apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.versions)
    cleanup
    base
}

allprojects {
    group = PUBLISHING_GROUP
    repositories {
        google() // Make sure this line is included
        mavenCentral()
    }
}

val detektFormatting = libs.detekt.formatting

subprojects {
    apply {
        plugin("io.gitlab.arturbosch.detekt")
    }

    detekt {
        config.from(rootProject.files("config/detekt/detekt.yml"))
    }

    dependencies {
        detektPlugins(detektFormatting)
    }
}

tasks {
    withType<DependencyUpdatesTask>().configureEach {
        rejectVersionIf {
            candidate.version.isStableVersion().not()
        }
    }
}
