gradle.beforeProject {
    val javaHome = System.getenv("JAVA_HOME") ?: "C:\\Program Files\\Java\\jdk-17.0.2"
    System.setProperty("org.gradle.java.home", javaHome)
}
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "androidx.navigation.safeargs.kotlin" ->
                    useModule("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.5")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "FoodSafetyApp"
include(":app")
include(":opencv")
