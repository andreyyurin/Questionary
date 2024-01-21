pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "questionary"
include(":ui:app", ":ui:base")
include(":data", ":domain", ":utils")
include(
    ":ui:splash", ":ui:login", ":ui:camera", ":ui:photoviewer", ":ui:profile",
    ":ui:subscriptions", ":ui:topquiz", ":ui:quiz", ":ui:createquiz", ":ui:quizresult",
    ":ui:onboarding"
)
