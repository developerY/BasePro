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
include(":core:database")
include(":feature:listings")
include(":feature:settings")
include(":core:ui")
include(":feature:home")
include(":feature:camera")
include(":feature:maps")
include(":core:data")
include(":feature:places")
include(":feature:heatlh")
include(":feature:ble")
include(":core:model")
include(":core:util")
include(":feature:wearos:home")
include(":feature:wearos:health")
include(":feature:wearos:sleepwatch")
include(":feature:wearos:drunkwatch")
include(":feature:shotime")
include(":feature:alarm")
include(":feature:bike")
include(":feature:weather")