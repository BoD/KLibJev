plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.serialization)
}

dependencies {
  // Kotlin
  implementation(libs.kotlinx.coroutines.jdk9)

  // Logging
  implementation(libs.klibnanolog)

  // Serialization
  implementation(libs.kotlinx.serialization.json)

  // Library
  implementation(project(":klibjev"))
}

kotlin {
  jvmToolchain(25)
}
