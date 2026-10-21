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

        // Serialization
        implementation(libs.kotlinx.serialization.json)

        // Logging
        implementation(libs.klibnanolog)

        // Kotlinx IO
        api(libs.kotlinx.io)
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
        // Ktor curl
        implementation(libs.ktor.client.curl)
      }
    }
  }
}

Librarian.module(project)
