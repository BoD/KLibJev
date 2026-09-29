plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.serialization)
}

dependencies {
  // Kotlin
  implementation(libs.kotlinx.coroutines.jdk9)

  // Logging
  implementation(libs.klibnanolog)

  // Date time
  implementation(libs.kotlinx.datetime)

  // Serialization
  implementation(libs.kotlinx.serialization.json)

  // Library
  implementation(project(":klibjev"))
}
