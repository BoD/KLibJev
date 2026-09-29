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

import kotlinx.serialization.Serializable
import org.jraf.klibjev.client.JevClient
import org.jraf.klibjev.client.JevClient.Configuration
import org.jraf.klibjev.client.JevClient.Configuration.Http
import org.jraf.klibjev.client.JevClient.Configuration.Http.LoggingLevel
import org.jraf.klibjev.client.evaluate
import org.jraf.klibjev.model.Choice
import org.jraf.klibjev.model.Noul
import org.jraf.klibjev.model.Question.Choice.Option
import org.jraf.klibjev.model.Score
import org.jraf.klibjev.model.State

suspend fun main(av: Array<String>) {
  JevClient(Configuration(apiKey = av[0], http = Http(loggingLevel = LoggingLevel.ALL))).use { jevClient ->
    val answers = jevClient.evaluate(
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
    println("Answers: $answers")
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
