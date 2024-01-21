plugins {
    id ("com.android.library")
    id ("org.jetbrains.kotlin.android")
}

android {
    namespace = "ru.sad.camera"
}

apply {
    from("$rootDir/ui/base_ui_module_config.gradle")
    from("$rootDir/ui/base_ui_dependencies.gradle")
}

dependencies {
    val cameraxVersion = "1.3.0"
    // CameraX core library using camera2 implementation
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    // CameraX Lifecycle Library
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    // CameraX View class
    implementation("androidx.camera:camera-view:$cameraxVersion")

    implementation(project(":utils"))
}