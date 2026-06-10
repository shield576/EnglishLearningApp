// settings.gradle.kts

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// ここでバージョンカタログを有効化します
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS") // この行があるか確認

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "EnglishLearningApp"
include(":app")
