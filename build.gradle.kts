buildscript {
    val compose_ui_version by extra("1.7.6")
    val agp_version by extra("8.7.2")
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.7.2" apply false
    id("com.android.library") version "7.4.2" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
    id("com.google.dagger.hilt.android") version "2.54" apply false
    id("com.google.devtools.ksp") version "2.1.0-1.0.28" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}