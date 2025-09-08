import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

group = "io.github.remmerw"
version = "0.1.7"


kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }


    jvm()
    // iosX64()
    // iosArm64()
    // iosSimulatorArm64()


    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.io.core)
                implementation(libs.saga)

                implementation(libs.lifecycle)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.materialIconsExtended)

                implementation(libs.coil.compose)
                implementation(libs.coil.network.ktor)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)
                implementation(libs.androidx.ui.test.junit4)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.androidx.core)
                implementation(libs.androidx.ui.test.junit4)
                implementation(libs.androidx.ui.test.manifest)
            }
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.androidx.runner)
            implementation(libs.androidx.core)
            implementation(libs.androidx.ui.test.junit4)
            implementation(libs.androidx.ui.test.manifest)
        }

        androidMain.dependencies {
            // Ktor client dependency required for Coil
            implementation(libs.ktor.client.android)
        }


        appleMain.dependencies {
            // Ktor client dependency required for iOS
            implementation(libs.ktor.client.darwin)
        }

        // alternatively jvmMain
        jvmMain.dependencies {
            // Ktor client dependency required for JVM/Desktop
            implementation(libs.ktor.client.java)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}


android {
    namespace = "io.github.remmerw.thor"
    compileSdk = 36
    defaultConfig {
        minSdk = 27
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}



mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "thor", version.toString())

    pom {
        name = "thor"
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
