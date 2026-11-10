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

import org.jetbrains.kotlinx.library.api.watchdog.ExemptionReason
import org.jetbrains.kotlinx.library.api.watchdog.IntentionallyInconsistentParameterOrder
import org.jetbrains.kotlinx.library.api.watchdog.IntentionallyOpen
import org.jraf.klibjev.InternalKLibJevApi
import org.jraf.klibjev.client.JevClient.Configuration
import org.jraf.klibjev.client.JevClient.Configuration.Http
import org.jraf.klibjev.internal.client.ConfigurationImpl
import org.jraf.klibjev.internal.client.ConfigurationImpl.HttpImpl
import org.jraf.klibjev.internal.client.ConfigurationImpl.HttpImpl.ProxyImpl
import org.jraf.klibjev.internal.client.JevClientImpl
import org.jraf.klibjev.model.Asset
import org.jraf.klibjev.model.EvaluateResponse
import org.jraf.klibjev.model.Question
import org.jraf.klibjev.model.State

@SubclassOptInRequired(InternalKLibJevApi::class)
public interface JevClient : AutoCloseable {
  @IntentionallyOpen(reason = ExemptionReason.API_DESIGN)
  public interface Configuration {
    public val apiKey: String
    public val model: String
    public val http: Http

    @IntentionallyOpen(reason = ExemptionReason.API_DESIGN)
    public interface Http {
      public val urlBase: String
      public val loggingLevel: LoggingLevel
      public val logger: (message: String) -> Unit
      public val proxy: Proxy?
      public val bypassSslChecks: Boolean

      @IntentionallyOpen(reason = ExemptionReason.API_DESIGN)
      public interface Proxy {
        public val host: String
        public val port: Int
      }

      public enum class LoggingLevel {
        /**
         * No logs.
         */
        NONE,
        INFO,
        HEADERS,
        BODY,
        ALL,
      }

      public companion object {
        public fun Proxy(
          host: String,
          port: Int,
        ): Proxy = ProxyImpl(
          host = host,
          port = port,
        )
      }
    }

    public companion object {
      public fun Http(
        urlBase: String = "https://api.typesafe.ai/v1/systemone",
        loggingLevel: Http.LoggingLevel = Http.LoggingLevel.NONE,
        logger: (message: String) -> Unit = { message -> println(message) },
        proxy: Http.Proxy? = null,
        bypassSslChecks: Boolean = false,
      ): Http = HttpImpl(
        urlBase = urlBase,
        loggingLevel = loggingLevel,
        logger = logger,
        proxy = proxy,
        bypassSslChecks = bypassSslChecks,
      )
    }
  }

  public suspend fun evaluate(
    state: State,
    assets: Set<Asset>,
    questions: Set<Question>,
  ): Result<EvaluateResponse>

  public companion object {
    public fun Configuration(
      apiKey: String,
      model: String = "jev-latest",
      http: Http = Configuration.Http(),
    ): Configuration = ConfigurationImpl(
      apiKey = apiKey,
      model = model,
      http = http,
    )
  }
}

public fun JevClient(configuration: Configuration): JevClient = JevClientImpl(configuration)

@IntentionallyInconsistentParameterOrder(reason = ExemptionReason.API_DESIGN)
public suspend fun JevClient.evaluate(
  state: State,
  assets: Set<Asset>,
  vararg questions: Question,
): Result<EvaluateResponse> = evaluate(state = state, questions = questions.toSet(), assets = assets)

@IntentionallyInconsistentParameterOrder(reason = ExemptionReason.API_DESIGN)
public suspend fun JevClient.evaluate(
  state: State,
  vararg questions: Question,
): Result<EvaluateResponse> = evaluate(state = state, questions = questions.toSet(), assets = emptySet())
