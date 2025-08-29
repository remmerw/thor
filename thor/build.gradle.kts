import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.vanniktech.mavenPublish)
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

                implementation(libs.androidx.sqlite.bundled)
                implementation(libs.androidx.room.runtime)

                implementation(libs.androidx.datastore.preferences.core)
                implementation(libs.androidx.datastore.preferences)

                implementation(libs.dagr)

                api(libs.idun)
                api(libs.borr)

                implementation(libs.rhino)
                implementation(libs.sac)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        jvmMain {
            dependencies {
                implementation("com.squareup.okhttp:okhttp:2.2.0")
                implementation("org.bouncycastle:bcprov-jdk15to18:1.64")
                implementation("org.bouncycastle:bcmail-jdk15to18:1.64")
                implementation("net.sf.cssbox:jstyleparser:1.23")
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
            }
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.androidx.runner)
            implementation(libs.androidx.core)
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



dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    //add("kspIosX64", libs.androidx.room.compiler)
    //add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    //add("kspIosArm64", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
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
