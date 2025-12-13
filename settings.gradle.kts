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
include(":feature:alarm")
include(":feature:weather")
include(":feature:qrscanner")
include(":feature:nfc")
include(":feature:ml")

// Applications
include(":applications:home")
include(":applications:medtime")
include(":applications:photodo")
include(":applications:ashbike")
include(":applications:ashbike:database")
include(":applications:ashbike:features:trips")
include(":applications:ashbike:features:settings")
include(":applications:ashbike:features:main")
include(":applications:rxdigita")
include(":applications:rxdigita:features:main")
include(":applications:rxdigita:features:settings")
include(":applications:rxdigita:features:medlist")
include(":applications:rxtrack")
include(":applications:rxtrack:features:main")
include(":applications:rxtrack:features:medlist")
include(":applications:rxtrack:features:settings")
include(":applications:photodo:features:home")
include(":applications:photodo:features:photodolist")
include(":applications:photodo:features:settings")
include(":feature:nav3")
include(":applications:ashbike:benchmark")
include(":applications:ashbike:features:core")
include(":applications:photodo:db")
include(":feature:material3")
include(":applications:photodo:core")
include(":applications:ashbike:apps:mobile")
include(":applications:ashbike:apps:wear")
include(":applications:ashbike:apps:xr")
