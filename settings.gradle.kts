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
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BasePro"
include(":app")
include(":core:data")
include(":feature:listings")
include(":feature:settings")
include(":core:ui")
include(":feature:home")
include(":feature:camera")
include(":feature:maps")
include(":core:network")
include(":feature:places")
