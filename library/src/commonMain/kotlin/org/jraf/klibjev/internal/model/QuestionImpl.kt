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

package org.jraf.klibjev.internal.model

import org.jraf.klibjev.model.Question
import kotlin.jvm.JvmInline

internal data class NoulImpl(
  override val instructions: Question.Instructions,
  override val criteria: Question.Noul.Criteria?,
) : Question.Noul

internal data class CriteriaImpl(
  override val `true`: String,
  override val `false`: String,
) : Question.Noul.Criteria

internal data class ChoiceImpl(
  override val instructions: Question.Instructions,
  override val options: Set<Question.Choice.Option>,
) : Question.Choice {
  init {
    require(options.size in 2..255) { "options.size must be in 2..255, was ${options.size}" }
  }
}

internal data class OptionImpl(
  override val name: String,
  override val description: String?,
) : Question.Choice.Option

internal data class ScoreImpl(
  override val instructions: Question.Instructions,
  override val levels: List<Question.Score.Level>,
) : Question.Score {
  init {
    require(levels.size in 2..10) { "levels.size must be in 2..10, was ${levels.size}" }
  }
}

@JvmInline
internal value class ScoreLevelStringImpl(override val value: String) : Question.Score.Level.String

internal data class ScoreLevelObjectImpl<T : Any>(override val value: T) : Question.Score.Level.Object<T>

internal data class BoxImpl(override val instructions: Question.Instructions) : Question.Box

@JvmInline
internal value class InstructionsStringImpl(override val value: String) : Question.Instructions.String

@JvmInline
internal value class InstructionsStringListImpl(override val value: List<String>) : Question.Instructions.StringList

internal data class InstructionsObjectImpl<T : Any>(override val value: T) : Question.Instructions.Object<T>
