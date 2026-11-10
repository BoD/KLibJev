import com.gradleup.librarian.gradle.Librarian
import org.jetbrains.kotlinx.library.api.watchdog.WatchdogSeverity

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.poko)
  alias(libs.plugins.kotlin.apiWatchdog)
}

kotlin {
  explicitApiWarning()

  jvm()
  macosArm64()

  compilerOptions {
    optIn.add("org.jraf.klibjev.InternalKLibJevApi")
  }

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

apiWatchdog {
  undocumentedPublicApi = WatchdogSeverity.NONE
  exhaustivePublicApi = WatchdogSeverity.NONE

  javaInterop {
    enabled = false
  }
}

Librarian.module(project)
