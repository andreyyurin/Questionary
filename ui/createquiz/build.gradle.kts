plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "ru.sad.createquiz"
}

apply {
    from("$rootDir/ui/base_ui_module_config.gradle")
    from("$rootDir/ui/base_ui_dependencies.gradle")
}

dependencies {
    implementation("com.github.chivorns:smartmaterialspinner:1.5.0")
}