# KLibJev

[![Maven Central](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Forg%2Fjraf%2Fklibjev%2Fklibjev-jvm%2Fmaven-metadata.xml&style=flat-square&label=maven-central&color=%235C96B2&strategy=latestProperty)](https://central.sonatype.com/artifact/org.jraf.klibjev/klibjev)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[![Snapshots](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Forg%2Fjraf%2Fklibjev%2Fklibjev-jvm%2Fmaven-metadata.xml&style=flat-square&label=snapshots&color=%2315252D&strategy=latestProperty)](https://central.sonatype.com/repository/maven-snapshots/org/jraf/klibjev/klibjev-jvm/maven-metadata.xml)

A [Jev](https://docs.typesafe.ai/introduction) API client library for Kotlin.

KLibJev is a Kotlin Multiplatform client for accessing the Jev API. It handles authentication and supports evaluating text or structured
data with yes/no (`Noul`), multiple-choice (`Choice`), and scoring (`Score`) questions, and receive typed answers with probabilities.

Since v1.1.0, images and `Box` questions are also supported.

## Usage

Add the library to your Kotlin project:

```kotlin
dependencies {
  implementation("org.jraf.klibjev:klibjev:$latest_version")
}
```

Create a client with your Jev API key and call `evaluate`:

```kotlin
JevClient(JevClient.Configuration("YOUR_API_KEY")).use { client ->
  val state = State("Bonjour !")
  val question = Noul("Is this a greeting?")
  val answer = client.evaluate(state, question).getOrThrow().answers[question]
  println(answer)
}
```

`evaluate` returns a Kotlin `Result`.

See the [JVM sample](samples/sample-jvm/src/main/kotlin/Main.kt) for a more comprehensive example.
