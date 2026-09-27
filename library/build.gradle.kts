import com.gradleup.librarian.gradle.Librarian

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.poko)
}

kotlin {
  jvm()

  macosArm64()

  sourceSets {
    commonMain {
      dependencies {
        // Ktor
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.contentNegotiation)
        implementation(libs.ktor.client.auth)
        implementation(libs.ktor.client.logging)
        implementation(libs.ktor.serialization.kotlinx.json)

        // Okio
        implementation(libs.okio)

        // Date time
        implementation(libs.kotlinx.datetime)

        // Serialization
        implementation(libs.kotlinx.serialization.json)

        // Logging
        implementation(libs.klibnanolog)
      }
    }

    commonTest {
      dependencies {
        implementation(kotlin("test"))
        implementation(libs.kotlinx.coroutines.test)
        implementation(libs.apollo.mockserver)
      }
    }


    jvmMain {
      dependencies {
        // Coroutines
        implementation(libs.kotlinx.coroutines.jdk9)

        // Ktor OkHttp
        implementation(libs.ktor.client.okhttp)
      }
    }

    jvmTest {
      dependencies {
        implementation(libs.konsist)
      }
    }

    macosArm64Main {
      dependencies {
        // Ktor CIO
        implementation(libs.ktor.client.darwin)
      }
    }
  }
}

Librarian.module(project)
