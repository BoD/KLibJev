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

package org.jraf.klibjev.client

import org.jraf.klibjev.internal.client.JevClientImpl
import org.jraf.klibjev.model.Answers
import org.jraf.klibjev.model.Question
import org.jraf.klibjev.model.State

interface JevClient : AutoCloseable {
  class Configuration(
    val apiKey: String,
    val model: String = "jev-latest",
    val http: Http = Http(),
  ) {
    class Http(
      val urlBase: String = "https://api.typesafe.ai/v1/systemone",
      val loggingLevel: LoggingLevel = LoggingLevel.NONE,
      val proxy: Proxy? = null,
    ) {
      class Proxy(
        val host: String,
        val port: Int,
      )

      enum class LoggingLevel {
        /**
         * No logs.
         */
        NONE,
        INFO,
        HEADERS,
        BODY,
        ALL,
      }
    }
  }

  suspend fun evaluate(
    state: State,
    questions: Set<Question>,
  ): Result<Answers>
}

fun JevClient(configuration: JevClient.Configuration): JevClient = JevClientImpl(configuration)

suspend fun JevClient.evaluate(
  state: State,
  vararg questions: Question,
): Result<Answers> = evaluate(state, questions.toSet())
