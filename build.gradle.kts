// Top-level build file where you can add configuration options common to all sub-projects/modules.
// AGP 9+: Kotlin sudah built-in, jangan apply org.jetbrains.kotlin.android manual.
plugins {
    alias(libs.plugins.android.application) apply false
}