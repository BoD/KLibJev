/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2025-present Benoit 'BoD' Lubek (BoD@JRAF.org)
 * and contributors (https://github.com/BoD/klibjev/graphs/contributors)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import kotlinx.io.asSource
import kotlinx.io.buffered
import kotlinx.serialization.Serializable
import org.jraf.klibjev.client.JevClient
import org.jraf.klibjev.client.JevClient.Configuration
import org.jraf.klibjev.client.JevClient.Configuration.Http
import org.jraf.klibjev.client.JevClient.Configuration.Http.LoggingLevel
import org.jraf.klibjev.client.evaluate
import org.jraf.klibjev.model.Asset
import org.jraf.klibjev.model.Box
import org.jraf.klibjev.model.Choice
import org.jraf.klibjev.model.EvaluateResponse.Answer
import org.jraf.klibjev.model.Noul
import org.jraf.klibjev.model.Question.Choice.Option
import org.jraf.klibjev.model.Score
import org.jraf.klibjev.model.State
import java.io.File

suspend fun main(av: Array<String>) {
  JevClient(
    configuration = Configuration(
      apiKey = av[0],
      http = Http(
        loggingLevel = LoggingLevel.ALL,
//        proxy = Http.Proxy("localhost", 8888),
//        bypassSslChecks = true,
      ),
      model = "jev-preview",
    ),
  ).use { jevClient ->
    val answers1 = jevClient.evaluate(
      state = State("Bonjour !"),
      Noul(instructions = "Is this a greeting?"),

      Choice(
        instructions = "What is the language of this text?",
        Option("English"),
        Option("French"),
        Option("Spanish"),
      ),

      Score(
        instructions = ComplexInstructions(
          question = "How warm is this text?",
          moreDetails = "The temperature of the text can be interpreted as how friendly or welcoming it feels.",
        ),
        ComplexLevel("Very cold", listOf("hey", "yo", "sup")),
        ComplexLevel("Cold", listOf("Hello", "Hi", "Greetings")),
        ComplexLevel("Neutral", listOf("Good day", "Salutations")),
        ComplexLevel("Warm", listOf("Hey there!", "Hiya!", "Howdy!")),
        ComplexLevel("Very warm", listOf("Hello, friend!", "Hi there, buddy!", "Greetings, my dear!")),
      ),
    ).getOrThrow()
    println("Answers 1: $answers1")

    val answers2 = jevClient.evaluate(
      state = State("`image1`"),
      assets = setOf(
        Asset.Image(
          id = "image1",
          source = File("samples/sample-jvm/assets/strawberry.png").inputStream().asSource().buffered(),
        ),
      ),
      Choice(
        instructions = "What is the main object in the image?",
        Option("Apple"),
        Option("Banana"),
        Option("Strawberry"),
        Option("Orange"),
        Option("Raspberry"),
      ),
    ).getOrThrow()
    println("Answers 2: $answers2")

    val answers3 = jevClient.evaluate(
      state = State("`image1`"),
      assets = setOf(
        Asset.Image(
          id = "image1",
          source = File("samples/sample-jvm/assets/strawberry-raspberry.png").inputStream().asSource().buffered(),
        ),
      ),
      Box(
        instructions = "Raspberry",
      ),
    ).getOrThrow()
    val boxAnswer = answers3.answers.values.firstOrNull() as Answer.Box
    val imageWidth = 1568
    val imageHeight = 887
    println("Answers 3: top=${boxAnswer.top * imageHeight}, bottom=${boxAnswer.bottom * imageHeight}, left=${boxAnswer.left * imageWidth}, right=${boxAnswer.right * imageWidth}")
  }
}

@Serializable
private data class ComplexInstructions(
  val question: String,
  val moreDetails: String,
)

@Serializable
private data class ComplexLevel(
  val what: String,
  val examples: List<String>,
)
