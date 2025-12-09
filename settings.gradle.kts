pluginManagement {
    includeBuild("build-logic")
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

rootProject.name = "NIADemo"
include(":app")
include(":core")
include(":core:common")
include(":sync")
include(":core:analytics")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":core:model")
include(":core:datastoreProto")
include(":core:notifications")
include(":core:network")
include(":feature")
include(":feature:bookmarks")
include(":core:testing")
include(":core:ui")
include(":core:designsystem")
include(":lint")
include(":feature:foryou")
include(":core:domain")
include(":feature:interests")
include(":feature:search")
include(":feature:setting")
include(":feature:topic")
include(":sync:work")
