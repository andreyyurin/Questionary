plugins {
    id ("com.android.library")
    id ("org.jetbrains.kotlin.android")
}

android {
    namespace = "ru.sad.splash"
}

apply {
    from("$rootDir/ui/base_ui_module_config.gradle")
    from("$rootDir/ui/base_ui_dependencies.gradle")
}