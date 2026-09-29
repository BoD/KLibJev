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

package org.jraf.klibjev.internal.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
internal class JsonEvaluateResponse(
  val model: String,
  val answers: Map<String, JsonAnswer>,

  // TODO usage
  //val usage: JsonUsage,
)

@Serializable
internal sealed interface JsonAnswer {
  @Serializable
  @SerialName("noul")
  class Noul(
    val noul: Double,
  ) : JsonAnswer

  @Serializable
  @SerialName("choice")
  class Choice(
    val choice: String,
    val probabilities: Map<String, Double>,
    val confidence: Double,
  ) : JsonAnswer

  @Serializable
  @SerialName("score")
  class Score(
    val score: Double,
    // Values can either be String or Object
    val legend: Map<String, JsonElement>,
    val probabilities: Map<String, Double>,
    val confidence: Double,
  ) : JsonAnswer
}
