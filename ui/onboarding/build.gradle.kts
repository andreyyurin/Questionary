plugins {
    id ("com.android.library")
    id ("org.jetbrains.kotlin.android")
}

android {
    namespace = "ru.sad.onboarding"
}

apply {
    from("$rootDir/ui/base_ui_module_config.gradle")
    from("$rootDir/ui/base_ui_dependencies.gradle")
}

dependencies {
    implementation("jp.wasabeef:glide-transformations:4.3.0")
}