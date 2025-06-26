import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "io.github.remmerw"
version = "0.2.1"


kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }


    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()


    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.androidx.sqlite.bundled)
                implementation(libs.androidx.room.runtime)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.io.core)
                implementation(libs.uri.kmp)
                implementation(libs.androidx.datastore.preferences.core)
                implementation(libs.androidx.datastore.preferences)
                implementation(libs.atomicfu)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.materialIconsExtended)
                implementation(libs.qrose)
                implementation(libs.lifecycle)

                implementation("io.github.remmerw:asen:0.2.7")
                implementation("io.github.remmerw:idun:0.2.7")

                implementation("io.github.vinceglb:filekit-core:0.10.0-beta04")
                implementation("io.github.vinceglb:filekit-dialogs:0.10.0-beta04")
                implementation("io.github.vinceglb:filekit-dialogs-compose:0.10.0-beta04")


                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

                implementation(libs.connectivity.core)
                implementation(libs.connectivity.compose)

            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        iosMain {
            dependencies {
                implementation(libs.connectivity.device)
                implementation(libs.connectivity.compose.device)
                implementation(libs.connectivity.apple)
            }
        }

        iosArm64Main {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-iosarm64:1.10.2")
            }
        }

        iosSimulatorArm64Main {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-iossimulatorarm64:1.10.2")
            }
        }

        iosX64Main {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-iosx64:1.10.2")
            }
        }
        jvmMain {
            dependencies {
                implementation(compose.desktop.common)
                implementation(libs.connectivity.http)
                implementation(libs.connectivity.compose.http)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation("androidx.test:runner:1.6.2")
                implementation("androidx.test:core:1.6.1")
            }
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("androidx.test:runner:1.6.2")
            implementation("androidx.test:core:1.6.1")
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.work.runtime)
                implementation(libs.connectivity.device)
                implementation(libs.connectivity.compose.device)
                implementation(libs.connectivity.android)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
            }
        }
    }
}


android {
    namespace = "io.github.remmerw.thor"
    compileSdk = 36
    defaultConfig {
        minSdk = 27
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}



dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
}



mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), "thor", version.toString())

    pom {
        name = "odin"
        description = "API library for Thor application"
        inceptionYear = "2025"
        url = "https://github.com/remmerw/thor/"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "remmerw"
                name = "Remmer Wilts"
                url = "https://github.com/remmerw/"
            }
        }
        scm {
            url = "https://github.com/remmerw/thor/"
            connection = "scm:git:git://github.com/remmerw/thor.git"
            developerConnection = "scm:git:ssh://git@github.com/remmerw/thor.git"
        }
    }
}
