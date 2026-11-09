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
import kotlinx.serialization.Serializable
import org.jraf.klibjev.model.Question.Box
import org.jraf.klibjev.model.Question.Choice
import org.jraf.klibjev.model.Question.Instructions
import org.jraf.klibjev.model.Question.Noul
import org.jraf.klibjev.model.Question.Score
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName

public sealed interface Question {
  public val instructions: Instructions

  @Poko
  public class Noul(
    public override val instructions: Instructions,
    public val criteria: Criteria? = null,
  ) : Question {
    @Poko
    public class Criteria(
      public val `true`: String,
      public val `false`: String,
    )
  }

  @Poko
  public class Choice(
    public override val instructions: Instructions,

    /**
     * Min: 2, max: 255.
     */
    public val options: Set<Option>,
  ) : Question {
    init {
      require(options.size in 2..255) { "options.size must be in 2..255, was ${options.size}" }
    }

    @Poko
    public class Option(
      public val name: String,
      public val description: String? = null,
    )
  }

  @Poko
  public class Score(
    public override val instructions: Instructions,

    /**
     * Min: 2, max: 10.
     */
    public val levels: List<Level>,
  ) : Question {
    init {
      require(levels.size in 2..10) { "levels.size must be in 2..10, was ${levels.size}" }
    }

    public sealed interface Level {
      @JvmInline
      public value class String(public val value: kotlin.String) : Level

      /**
       * [T] must be [Serializable].
       */
      @Poko
      public class Object<T : Any>(public val value: T) : Level
    }
  }

  @Poko
  public class Box(
    public override val instructions: Instructions,
  ) : Question

  public sealed interface Instructions {
    @JvmInline
    public value class String(public val value: kotlin.String) : Instructions


    @JvmInline
    public value class StringList(public val value: List<kotlin.String>) : Instructions

    /**
     * [T] must be [Serializable].
     */
    @Poko
    public class Object<T : Any>(public val value: T) : Instructions
  }
}

// Noul

public fun Noul(instructions: String, criteria: Noul.Criteria? = null): Noul = Noul(Instructions.String(instructions), criteria)
public fun Noul(instructions: List<String>, criteria: Noul.Criteria? = null): Noul = Noul(Instructions.StringList(instructions), criteria)

/**
 * [T] must be [Serializable].
 */
public fun <T : Any> Noul(instructions: T, criteria: Noul.Criteria? = null): Noul = Noul(Instructions.Object(instructions), criteria)


// Choice

public fun Choice(instructions: String, options: Set<Choice.Option>): Choice = Choice(Instructions.String(instructions), options)
public fun Choice(instructions: List<String>, options: Set<Choice.Option>): Choice = Choice(Instructions.StringList(instructions), options)

/**
 * [T] must be [Serializable].
 */
public fun <T : Any> Choice(instructions: T, options: Set<Choice.Option>): Choice = Choice(Instructions.Object(instructions), options)

public fun Choice(instructions: String, vararg options: Choice.Option): Choice = Choice(Instructions.String(instructions), options.toSet())
public fun Choice(instructions: List<String>, vararg options: Choice.Option): Choice =
  Choice(Instructions.StringList(instructions), options.toSet())

/**
 * [T] must be [Serializable].
 */
public fun <T : Any> Choice(instructions: T, vararg options: Choice.Option): Choice =
  Choice(Instructions.Object(instructions), options.toSet())


// Score (Strings)

public fun Score(instructions: String, levels: List<String>): Score =
  Score(Instructions.String(instructions), levels.map { Score.Level.String(it) })

public fun Score(instructions: List<String>, levels: List<String>): Score =
  Score(Instructions.StringList(instructions), levels.map { Score.Level.String(it) })

/**
 * [T] must be [Serializable].
 */
public fun <T : Any> Score(instructions: T, levels: List<String>): Score =
  Score(Instructions.Object(instructions), levels.map { Score.Level.String(it) })

public fun Score(instructions: String, vararg levels: String): Score =
  Score(Instructions.String(instructions), levels.map { Score.Level.String(it) })

public fun Score(instructions: List<String>, vararg levels: String): Score =
  Score(Instructions.StringList(instructions), levels.map { Score.Level.String(it) })

/**
 * [T] must be [Serializable].
 */
public fun <T : Any> Score(instructions: T, vararg levels: String): Score =
  Score(Instructions.Object(instructions), levels.map { Score.Level.String(it) })


// Score (Objects)

/**
 * [L] must be [Serializable].
 */
@JvmName("ScoreObjects")
public fun <L : Any> Score(instructions: String, levels: List<L>): Score =
  Score(Instructions.String(instructions), levels.map { Score.Level.Object(it) })

/**
 * [L] must be [Serializable].
 */
@JvmName("ScoreObjects")
public fun <L : Any> Score(instructions: List<String>, levels: List<L>): Score =
  Score(Instructions.StringList(instructions), levels.map { Score.Level.Object(it) })

/**
 * [L] and [T] must be [Serializable].
 */
@JvmName("ScoreObjects")
public fun <T : Any, L : Any> Score(instructions: T, levels: List<L>): Score =
  Score(Instructions.Object(instructions), levels.map { Score.Level.Object(it) })

/**
 * [L] must be [Serializable].
 */
public fun <L : Any> Score(instructions: String, vararg levels: L): Score =
  Score(Instructions.String(instructions), levels.map { Score.Level.Object(it) })

/**
 * [L] must be [Serializable].
 */
public fun <L : Any> Score(instructions: List<String>, vararg levels: L): Score =
  Score(Instructions.StringList(instructions), levels.map { Score.Level.Object(it) })

/**
 * [L] and [T] must be [Serializable].
 */
public fun <T : Any, L : Any> Score(instructions: T, vararg levels: L): Score =
  Score(Instructions.Object(instructions), levels.map { Score.Level.Object(it) })


// Box

public fun Box(instructions: String): Box = Box(Instructions.String(instructions))
public fun Box(instructions: List<String>): Box = Box(Instructions.StringList(instructions))

/**
 * [T] must be [Serializable].
 */
public fun <T : Any> Box(instructions: T): Box = Box(Instructions.Object(instructions))
