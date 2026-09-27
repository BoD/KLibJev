plugins {
  alias(libs.plugins.kotlin.jvm)
}

dependencies {
  // Kotlin
  implementation(libs.kotlinx.coroutines.jdk9)

  // Logging
  implementation(libs.klibnanolog)

  // Date time
  implementation(libs.kotlinx.datetime)

  // Library
  implementation(project(":klibjev"))
}
