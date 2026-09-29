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
import org.jraf.klibjev.model.Question.Choice
import org.jraf.klibjev.model.Question.Instructions
import org.jraf.klibjev.model.Question.Noul
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName

sealed interface Question {
  val instructions: Instructions

  @Poko
  class Noul(
    override val instructions: Instructions,
    val criteria: Criteria? = null,
  ) : Question {
    @Poko
    class Criteria(
      val `true`: String,
      val `false`: String,
    )
  }

  @Poko
  class Choice(
    override val instructions: Instructions,

    /**
     * Min: 2, max: 255.
     */
    val options: Set<Option>,
  ) : Question {
    init {
      require(options.size in 2..255) { "options.size must be in 2..255, was ${options.size}" }
    }

    @Poko
    class Option(
      val name: String,
      val description: String? = null,
    )
  }

  @Poko
  class Score(
    override val instructions: Instructions,

    /**
     * Min: 2, max: 10.
     */
    val levels: List<Level>,
  ) : Question {
    init {
      require(levels.size in 2..10) { "levels.size must be in 2..10, was ${levels.size}" }
    }

    sealed interface Level {
      @JvmInline
      value class String(val value: kotlin.String) : Level

      /**
       * [T] must be marked [kotlinx.serialization.Serializable].
       */
      @Poko
      class Object<T : Any>(val value: T) : Level
    }
  }

  sealed interface Instructions {
    @JvmInline
    value class String(val value: kotlin.String) : Instructions


    @JvmInline
    value class StringList(val value: List<kotlin.String>) : Instructions

    /**
     * [T] must be marked [kotlinx.serialization.Serializable].
     */
    @Poko
    class Object<T : Any>(val value: T) : Instructions
  }
}

// Noul

fun Noul(instructions: String, criteria: Noul.Criteria? = null): Noul = Noul(Instructions.String(instructions), criteria)
fun Noul(instructions: List<String>, criteria: Noul.Criteria? = null): Noul = Noul(Instructions.StringList(instructions), criteria)

/**
 * [T] must be marked [kotlinx.serialization.Serializable].
 */
fun <T : Any> Noul(instructions: T, criteria: Noul.Criteria? = null): Noul = Noul(Instructions.Object(instructions), criteria)


// Choice

fun Choice(instructions: String, options: Set<Choice.Option>): Choice = Choice(Instructions.String(instructions), options)
fun Choice(instructions: List<String>, options: Set<Choice.Option>): Choice = Choice(Instructions.StringList(instructions), options)

/**
 * [T] must be marked [kotlinx.serialization.Serializable].
 */
fun <T : Any> Choice(instructions: T, options: Set<Choice.Option>): Choice = Choice(Instructions.Object(instructions), options)

fun Choice(instructions: String, vararg options: Choice.Option): Choice = Choice(Instructions.String(instructions), options.toSet())
fun Choice(instructions: List<String>, vararg options: Choice.Option): Choice =
  Choice(Instructions.StringList(instructions), options.toSet())

/**
 * [T] must be marked [kotlinx.serialization.Serializable].
 */
fun <T : Any> Choice(instructions: T, vararg options: Choice.Option): Choice = Choice(Instructions.Object(instructions), options.toSet())


// Score (Strings)

fun Score(instructions: String, levels: List<String>): Question.Score =
  Question.Score(Instructions.String(instructions), levels.map { Question.Score.Level.String(it) })

fun Score(instructions: List<String>, levels: List<String>): Question.Score =
  Question.Score(Instructions.StringList(instructions), levels.map { Question.Score.Level.String(it) })

/**
 * [T] must be marked [kotlinx.serialization.Serializable].
 */
fun <T : Any> Score(instructions: T, levels: List<String>): Question.Score =
  Question.Score(Instructions.Object(instructions), levels.map { Question.Score.Level.String(it) })

fun Score(instructions: String, vararg levels: String): Question.Score =
  Question.Score(Instructions.String(instructions), levels.map { Question.Score.Level.String(it) })

fun Score(instructions: List<String>, vararg levels: String): Question.Score =
  Question.Score(Instructions.StringList(instructions), levels.map { Question.Score.Level.String(it) })

/**
 * [T] must be marked [kotlinx.serialization.Serializable].
 */
fun <T : Any> Score(instructions: T, vararg levels: String): Question.Score =
  Question.Score(Instructions.Object(instructions), levels.map { Question.Score.Level.String(it) })


// Score (Objects)

/**
 * [L] must be marked [kotlinx.serialization.Serializable].
 */
@JvmName("ScoreObjects")
fun <L : Any> Score(instructions: String, levels: List<L>): Question.Score =
  Question.Score(Instructions.String(instructions), levels.map { Question.Score.Level.Object(it) })

/**
 * [L] must be marked [kotlinx.serialization.Serializable].
 */
@JvmName("ScoreObjects")
fun <L : Any> Score(instructions: List<String>, levels: List<L>): Question.Score =
  Question.Score(Instructions.StringList(instructions), levels.map { Question.Score.Level.Object(it) })

/**
 * [L] and [T] must be marked [kotlinx.serialization.Serializable].
 */
@JvmName("ScoreObjects")
fun <T : Any, L : Any> Score(instructions: T, levels: List<L>): Question.Score =
  Question.Score(Instructions.Object(instructions), levels.map { Question.Score.Level.Object(it) })

/**
 * [L] must be marked [kotlinx.serialization.Serializable].
 */
fun <L : Any> Score(instructions: String, vararg levels: L): Question.Score =
  Question.Score(Instructions.String(instructions), levels.map { Question.Score.Level.Object(it) })

/**
 * [L] must be marked [kotlinx.serialization.Serializable].
 */
fun <L : Any> Score(instructions: List<String>, vararg levels: L): Question.Score =
  Question.Score(Instructions.StringList(instructions), levels.map { Question.Score.Level.Object(it) })

/**
 * [L] and [T] must be marked [kotlinx.serialization.Serializable].
 */
fun <T : Any, L : Any> Score(instructions: T, vararg levels: L): Question.Score =
  Question.Score(Instructions.Object(instructions), levels.map { Question.Score.Level.Object(it) })
