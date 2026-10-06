# KLibJev

A [Jev](https://docs.typesafe.ai/introduction) API client library for Kotlin.

KLibJev is a Kotlin Multiplatform client for accessing the Jev API. It handles authentication and supports evaluating text or structured
data with yes/no (`Noul`), multiple-choice (`Choice`), and scoring (`Score`) questions, and receive typed answers with probabilities.

## Usage

Add the library to your Kotlin project:

```kotlin
dependencies {
  implementation("org.jraf.klibjev:klibjev:$latest_version")
}
```

Create a client with your Jev API key and call `evaluate`:

```kotlin
JevClient(JevClient.Configuration(apiKey = "YOUR_API_KEY")).use { client ->
  val answers = client.evaluate(
    State("Bonjour !"),
    Noul(instructions = "Is this a greeting?"),
  ).getOrThrow().answers
  println(answers)
}
```

`evaluate` returns a Kotlin `Result`.

See the [JVM sample](samples/sample-jvm/src/main/kotlin/Main.kt) for a more comprehensive example.
