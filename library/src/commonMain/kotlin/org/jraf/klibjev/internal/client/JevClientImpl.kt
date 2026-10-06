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

package org.jraf.klibjev.internal.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLBuilder
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.serializer
import org.jraf.klibjev.client.JevClient
import org.jraf.klibjev.client.JevClient.Configuration
import org.jraf.klibjev.internal.json.JsonAnswer
import org.jraf.klibjev.internal.json.JsonEvaluateRequest
import org.jraf.klibjev.internal.json.JsonEvaluateResponse
import org.jraf.klibjev.internal.json.JsonQuestion
import org.jraf.klibjev.internal.model.ChoiceImpl
import org.jraf.klibjev.internal.model.EvaluateResponseImpl
import org.jraf.klibjev.internal.model.NoulImpl
import org.jraf.klibjev.internal.model.ScoreImpl
import org.jraf.klibjev.internal.model.UsageImpl
import org.jraf.klibjev.model.EvaluateResponse
import org.jraf.klibjev.model.Question
import org.jraf.klibjev.model.State
import org.jraf.klibnanolog.logd

internal class JevClientImpl(
  private var configuration: Configuration,
) : JevClient {
  private val service: JevService by lazy {
    JevService(httpClient = provideHttpClient(), urlBase = configuration.http.urlBase)
  }

  private fun provideHttpClient(): HttpClient {
    return HttpClient {
      install(ContentNegotiation) {
        json(
          Json {
            ignoreUnknownKeys = true
            useAlternativeNames = false
          },
        )
      }
      install(HttpTimeout) {
        requestTimeoutMillis = 60_000
        connectTimeoutMillis = 60_000
        socketTimeoutMillis = 60_000
      }
      engine {
        // Set up a proxy if requested
        configuration.http.proxy?.let { httpProxy ->
          proxy = ProxyBuilder.http(
            URLBuilder().apply {
              host = httpProxy.host
              port = httpProxy.port
            }.build(),
          )
        }
      }
      install(Auth) {
        bearer {
          loadTokens {
            BearerTokens(
              accessToken = configuration.apiKey,
              refreshToken = null,
            )
          }
        }
      }

      // Setup logging if requested
      if (configuration.http.loggingLevel != Configuration.Http.LoggingLevel.NONE) {
        install(Logging) {
          logger = object : Logger {
            override fun log(message: String) {
              logd(message)
            }
          }
          level = when (configuration.http.loggingLevel) {
            Configuration.Http.LoggingLevel.NONE -> LogLevel.NONE
            Configuration.Http.LoggingLevel.INFO -> LogLevel.INFO
            Configuration.Http.LoggingLevel.HEADERS -> LogLevel.HEADERS
            Configuration.Http.LoggingLevel.BODY -> LogLevel.BODY
            Configuration.Http.LoggingLevel.ALL -> LogLevel.ALL
          }
        }
      }
    }
  }

  override fun close() {
    service.close()
  }

  override suspend fun evaluate(
    state: State,
    questions: Set<Question>,
  ): Result<EvaluateResponse> {
    val questionIds: Map<String, Question> = questions.toList().mapIndexed { index, question -> "q$index" to question }.toMap()
    val jsonEvaluateRequest = JsonEvaluateRequest(
      model = configuration.model,
      state = state.toJsonState(),
      questions = questions.toJsonQuestionsMap(questionIds),
    )
    return runCatching {
      service.evaluate(jsonEvaluateRequest).toEvaluateResponse(questionIds)
    }
  }
}

private fun State.toJsonState(): JsonElement {
  return when (this) {
    is State.String -> {
      JsonPrimitive(this.value)
    }

    is State.StringList -> {
      JsonArray(this.value.map { JsonPrimitive(it) })
    }

    is State.Object<*> -> {
      encodeToJsonElement(this.value)
    }
  }
}

private fun Set<Question>.toJsonQuestionsMap(questionIds: Map<String, Question>): Map<String, JsonQuestion> {
  return this.associate { question ->
    questionIds.entries.first { it.value == question }.key to when (question) {
      is Question.Noul -> JsonQuestion.Noul(
        instructions = question.instructions.toJsonInstructions(),
        criteria = question.criteria?.let { JsonQuestion.Noul.JsonCriteria(it.`true`, it.`false`) },
      )

      is Question.Choice -> JsonQuestion.Choice(
        instructions = question.instructions.toJsonInstructions(),
        criteria = question.options.associate { it.name to it.description },
      )

      is Question.Score -> JsonQuestion.Score(
        instructions = question.instructions.toJsonInstructions(),
        criteria = question.levels.map { it.toJsonElement() },
      )
    }
  }
}

private fun Question.Instructions.toJsonInstructions(): JsonElement {
  return when (this) {
    is Question.Instructions.String -> {
      JsonPrimitive(this.value)
    }

    is Question.Instructions.StringList -> {
      JsonArray(this.value.map { JsonPrimitive(it) })
    }

    is Question.Instructions.Object<*> -> {
      encodeToJsonElement(this.value)
    }
  }
}

private fun Question.Score.Level.toJsonElement(): JsonElement {
  return when (this) {
    is Question.Score.Level.String -> {
      JsonPrimitive(this.value)
    }

    is Question.Score.Level.Object<*> -> {
      encodeToJsonElement(this.value)
    }
  }
}

private fun JsonEvaluateResponse.toEvaluateResponse(questionIds: Map<String, Question>): EvaluateResponse {
  return EvaluateResponseImpl(
    model = this.model,
    answers = this.answers.map { (questionId, answer) ->
      val question = questionIds[questionId]
      (question ?: error("Unknown question ID: $questionId")) to answer.toAnswer(question)
    }.toMap(),
    usage = UsageImpl(
      inputTokens = this.usage.input_tokens,
      outputTokens = this.usage.output_tokens,
    ),
  )
}

private fun JsonAnswer.toAnswer(question: Question): EvaluateResponse.Answer {
  return when (this) {
    is JsonAnswer.Noul -> NoulImpl(
      value = this.noul,
    )

    is JsonAnswer.Choice -> ChoiceImpl(
      highestProbability = (question as Question.Choice).options.first { it.name == this.choice },
      probabilities = this.probabilities.map { (optionName, probability) ->
        val option = question.options.firstOrNull { it.name == optionName }
          ?: error("Unknown option name: $optionName")
        option to probability
      }.toMap(),
      confidence = this.confidence,
    )

    is JsonAnswer.Score -> ScoreImpl(
      value = this.score,
      probabilities = this.probabilities.map { (levelIndex, probability) ->
        val level = when (val levelJsonElement: JsonElement = this.legend[levelIndex]!!) {
          is JsonPrimitive -> Question.Score.Level.String(levelJsonElement.content)
          is JsonObject -> {
            // Find back the level from the question by comparing the JSON representations.
            // A bit hacky and not efficient, but this ensures we return the same instance as the one in the question.
            // We can avoid this if we trust the API to return the levels in the same order as the question, but I don't think it is guaranteed.
            val level =
              (question as Question.Score).levels.first { it is Question.Score.Level.Object<*> && encodeToJsonElement(it.value) == levelJsonElement }
            Question.Score.Level.Object(level)
          }

          else -> error("Unknown level JSON element type: ${levelJsonElement::class.simpleName}")
        }
        level to probability
      }.toMap(),
      confidence = this.confidence,
    )
  }
}

@Suppress("UNCHECKED_CAST")
@OptIn(InternalSerializationApi::class)
private fun encodeToJsonElement(o: Any): JsonElement {
  // A bit hacky - here the compiler doesn't know the type of the value, so we need to pass the serializer obtained dynamically
  return Json.encodeToJsonElement(o::class.serializer() as KSerializer<Any>, o)
}
