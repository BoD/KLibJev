/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2026-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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

import org.jraf.klibjev.client.JevClient

internal data class ConfigurationImpl(
  override val apiKey: String,
  override val model: String,
  override val http: JevClient.Configuration.Http,
) : JevClient.Configuration {
  internal data class HttpImpl(
    override val urlBase: String,
    override val loggingLevel: JevClient.Configuration.Http.LoggingLevel,
    override val logger: (message: String) -> Unit,
    override val proxy: JevClient.Configuration.Http.Proxy?,
    override val bypassSslChecks: Boolean,
  ) : JevClient.Configuration.Http {
    internal data class ProxyImpl(
      override val host: String,
      override val port: Int,
    ) : JevClient.Configuration.Http.Proxy
  }
}
