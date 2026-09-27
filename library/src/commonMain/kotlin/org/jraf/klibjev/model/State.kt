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

import kotlin.jvm.JvmInline

sealed interface State {
  @JvmInline
  value class StringState(val value: String) : State

  @JvmInline
  value class StringListState(val value: List<String>) : State

  /**
   * [T] must be marked [kotlinx.serialization.Serializable].
   */
  class ObjectState<T : Any>(val value: T) : State
}

fun State(value: String): State = State.StringState(value)
fun State(value: List<String>): State = State.StringListState(value)
fun <T : Any> State(value: T): State = State.ObjectState(value)
