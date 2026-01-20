rootProject.name = "HostelDada-KMP"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

// Core modules
include(":core:common")
include(":core:domain")
include(":core:data")

// Feature modules
include(":feature:auth")
include(":feature:snackcart")
include(":feature:roomie")

// Shared modules
include(":shared:di")
include(":shared:ui")
include(":shared:navigation")

// Platform applications
include(":androidApp")
include(":iosApp")
include(":desktopApp")
