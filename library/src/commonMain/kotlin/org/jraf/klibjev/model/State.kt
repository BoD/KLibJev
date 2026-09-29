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

package org.jraf.klibjev.model

import dev.drewhamilton.poko.Poko
import kotlin.jvm.JvmInline

sealed interface State {
  @JvmInline
  value class String(val value: kotlin.String) : State


  @JvmInline
  value class StringList(val value: List<kotlin.String>) : State

  /**
   * [T] must be marked [kotlinx.serialization.Serializable].
   */
  @Poko
  class Object<T : Any>(val value: T) : State
}

fun State(value: String): State = State.String(value)
fun State(value: List<String>): State = State.StringList(value)

/**
 * [T] must be marked [kotlinx.serialization.Serializable].
 */
fun <T : Any> State(value: T): State = State.Object(value)
