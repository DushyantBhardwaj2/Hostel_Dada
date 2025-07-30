plugins {
    kotlin("multiplatform") version "1.9.22"
    id("org.jetbrains.compose") version "1.5.12"
    kotlin("plugin.serialization") version "1.9.22"
}

group = "com.hosteldada"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
            binaries.executable()
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.web.core)
                implementation(compose.web.widgets)
                implementation(compose.runtime)
                implementation("dev.gitlive:firebase-kotlin-sdk:1.10.4")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
                implementation("io.github.kevinnzou:compose-web-material3:1.0.0")
            }
        }
        
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)
                implementation(npm("firebase", "10.7.1"))
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
                implementation("io.mockk:mockk:1.13.8")
                implementation("app.cash.turbine:turbine:1.0.0")
            }
        }
        
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
                implementation(npm("@jest/globals", "29.7.0"))
                implementation(npm("jest", "29.7.0"))
                implementation(npm("jest-environment-jsdom", "29.7.0"))
            }
        }
    }
}

// Compose Web configuration
compose.web {
    
}

// Testing configuration
tasks.withType<Test> {
    useJUnitPlatform()
}

// Build optimization
tasks.named("jsBrowserDevelopmentWebpack") {
    dependsOn("jsTestClasses")
}

tasks.named("jsBrowserProductionWebpack") {
    dependsOn("jsTest")
}
