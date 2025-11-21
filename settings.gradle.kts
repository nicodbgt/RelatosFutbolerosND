pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") } // Required for HaishinKit.kt
    }
    plugins {
        id("com.google.dagger.hilt.android") version "2.51.1" apply false
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven { url = uri("https://jitpack.io") } // Required for HaishinKit.kt
        mavenCentral()
    }
}

rootProject.name = "RelatosFutbolerosND"
include(":app")
