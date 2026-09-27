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

import org.jraf.klibjev.client.JevClient
import org.jraf.klibjev.client.JevClient.Configuration
import org.jraf.klibjev.client.JevClient.Configuration.Http
import org.jraf.klibjev.client.JevClient.Configuration.Http.LoggingLevel
import org.jraf.klibjev.model.Question
import org.jraf.klibjev.model.Question.Choice.Option
import org.jraf.klibjev.model.State

suspend fun main(av: Array<String>) {
  JevClient(Configuration(apiKey = av[0], http = Http(loggingLevel = LoggingLevel.ALL))).use { jevClient ->
    val state = State("Bonjour !")
    val questions = setOf(
      Question.Noul(instructions = "Is this a greeting?"),
      Question.Choice(
        instructions = "What is the language of this text?",
        options = setOf(Option("English"), Option("French"), Option("Spanish")),
      ),
      Question.Score(
        instructions = "How warm is this text?",
        levels = listOf("Very cold", "Cold", "Neutral", "Warm", "Very warm"),
      ),
    )
    println("Answers: ${jevClient.evaluate(state, questions).getOrThrow()}")
  }
}
