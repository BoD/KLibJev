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

import kotlinx.serialization.Serializable
import org.jraf.klibjev.InternalKLibJevApi
import org.jraf.klibjev.internal.model.StateObjectImpl
import org.jraf.klibjev.internal.model.StateStringImpl
import org.jraf.klibjev.internal.model.StateStringListImpl

public sealed interface State {
  @SubclassOptInRequired(InternalKLibJevApi::class)
  public interface String : State {
    public val value: kotlin.String
  }

  @SubclassOptInRequired(InternalKLibJevApi::class)
  public interface StringList : State {
    public val value: List<kotlin.String>
  }

  /**
   * [T] must be [Serializable].
   */
  @SubclassOptInRequired(InternalKLibJevApi::class)
  public interface Object<T : Any> : State {
    public val value: T
  }

  public companion object {
    public fun String(value: kotlin.String): String = StateStringImpl(value)

    public fun StringList(value: List<kotlin.String>): StringList = StateStringListImpl(value)

    /**
     * [T] must be [Serializable].
     */
    public fun <T : Any> Object(value: T): Object<T> = StateObjectImpl(value)
  }
}

public fun State(value: String): State = State.String(value)
public fun State(value: List<String>): State = State.StringList(value)

/**
 * [T] must be [Serializable].
 */
public fun <T : Any> State(value: T): State = State.Object(value)
