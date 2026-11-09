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
import org.jraf.klibjev.internal.model.BoxImpl
import org.jraf.klibjev.internal.model.ChoiceImpl
import org.jraf.klibjev.internal.model.CriteriaImpl
import org.jraf.klibjev.internal.model.InstructionsObjectImpl
import org.jraf.klibjev.internal.model.InstructionsStringImpl
import org.jraf.klibjev.internal.model.InstructionsStringListImpl
import org.jraf.klibjev.internal.model.NoulImpl
import org.jraf.klibjev.internal.model.OptionImpl
import org.jraf.klibjev.internal.model.ScoreImpl
import org.jraf.klibjev.internal.model.ScoreLevelObjectImpl
import org.jraf.klibjev.internal.model.ScoreLevelStringImpl
import org.jraf.klibjev.model.Question.Box
import org.jraf.klibjev.model.Question.Choice
import org.jraf.klibjev.model.Question.Instructions
import org.jraf.klibjev.model.Question.Noul
import org.jraf.klibjev.model.Question.Score
import kotlin.jvm.JvmName

public sealed interface Question {
  public val instructions: Instructions

  @SubclassOptInRequired(InternalKLibJevApi::class)
  public interface Noul : Question {
    public val criteria: Criteria?

    @SubclassOptInRequired(InternalKLibJevApi::class)
    public interface Criteria {
      public val `true`: String
      public val `false`: String
    }

    public companion object {
      public fun Criteria(`true`: String, `false`: String): Criteria = CriteriaImpl(`true`, `false`)
    }
  }

  @SubclassOptInRequired(InternalKLibJevApi::class)
  public interface Choice : Question {
    /**
     * Min: 2, max: 255.
     */
    public val options: Set<Option>

    @SubclassOptInRequired(InternalKLibJevApi::class)
    public interface Option {
      public val name: String
      public val description: String?
    }

    public companion object {
      public fun Option(name: String, description: String? = null): Option = OptionImpl(name, description)
    }
  }

  @SubclassOptInRequired(InternalKLibJevApi::class)
  public interface Score : Question {
    /**
     * Min: 2, max: 10.
     */
    public val levels: List<Level>

    public sealed interface Level {
      @SubclassOptInRequired(InternalKLibJevApi::class)
      public interface String : Level {
        public val value: kotlin.String
      }

      /**
       * [T] must be [Serializable].
       */
      @SubclassOptInRequired(InternalKLibJevApi::class)
      public interface Object<T : Any> : Level {
        public val value: T
      }

      public companion object {
        public fun String(value: kotlin.String): String = ScoreLevelStringImpl(value)

        /**
         * [T] must be [Serializable].
         */
        public fun <T : Any> Object(value: T): Object<T> = ScoreLevelObjectImpl(value)
      }
    }
  }

  @SubclassOptInRequired(InternalKLibJevApi::class)
  public interface Box : Question

  public sealed interface Instructions {
    @SubclassOptInRequired(InternalKLibJevApi::class)
    public interface String : Instructions {
      public val value: kotlin.String
    }

    @SubclassOptInRequired(InternalKLibJevApi::class)
    public interface StringList : Instructions {
      public val value: List<kotlin.String>
    }

    /**
     * [T] must be [Serializable].
     */
    @SubclassOptInRequired(InternalKLibJevApi::class)
    public interface Object<T : Any> : Instructions {
      public val value: T
    }

    public companion object {
      public fun String(value: kotlin.String): String = InstructionsStringImpl(value)

      public fun StringList(value: List<kotlin.String>): StringList = InstructionsStringListImpl(value)

      /**
       * [T] must be [Serializable].
       */
      public fun <T : Any> Object(value: T): Object<T> = InstructionsObjectImpl(value)
    }
  }

  public companion object {
    public fun Noul(instructions: Instructions, criteria: Noul.Criteria? = null): Noul = NoulImpl(instructions, criteria)

    public fun Choice(instructions: Instructions, options: Set<Choice.Option>): Choice = ChoiceImpl(instructions, options)

    public fun Score(instructions: Instructions, levels: List<Score.Level>): Score = ScoreImpl(instructions, levels)

    public fun Box(instructions: Instructions): Box = BoxImpl(instructions)
  }
}

// Noul

public fun Noul(instructions: String, criteria: Noul.Criteria? = null): Noul = Question.Noul(Instructions.String(instructions), criteria)
public fun Noul(instructions: List<String>, criteria: Noul.Criteria? = null): Noul =
  Question.Noul(Instructions.StringList(instructions), criteria)

/**
 * [T] must be [Serializable].
 */
public fun <T : Any> Noul(instructions: T, criteria: Noul.Criteria? = null): Noul =
  Question.Noul(Instructions.Object(instructions), criteria)


// Choice

public fun Choice(instructions: String, options: Set<Choice.Option>): Choice = Question.Choice(Instructions.String(instructions), options)
public fun Choice(instructions: List<String>, options: Set<Choice.Option>): Choice =
  Question.Choice(Instructions.StringList(instructions), options)

/**
 * [T] must be [Serializable].
 */
public fun <T : Any> Choice(instructions: T, options: Set<Choice.Option>): Choice =
  Question.Choice(Instructions.Object(instructions), options)

public fun Choice(instructions: String, vararg options: Choice.Option): Choice =
  Question.Choice(Instructions.String(instructions), options.toSet())
public fun Choice(instructions: List<String>, vararg options: Choice.Option): Choice =
  Question.Choice(Instructions.StringList(instructions), options.toSet())

/**
 * [T] must be [Serializable].
 */
public fun <T : Any> Choice(instructions: T, vararg options: Choice.Option): Choice =
  Question.Choice(Instructions.Object(instructions), options.toSet())


// Score (Strings)

public fun Score(instructions: String, levels: List<String>): Score =
  Question.Score(Instructions.String(instructions), levels.map { Score.Level.String(it) })

public fun Score(instructions: List<String>, levels: List<String>): Score =
  Question.Score(Instructions.StringList(instructions), levels.map { Score.Level.String(it) })

/**
 * [T] must be [Serializable].
 */
public fun <T : Any> Score(instructions: T, levels: List<String>): Score =
  Question.Score(Instructions.Object(instructions), levels.map { Score.Level.String(it) })

public fun Score(instructions: String, vararg levels: String): Score =
  Question.Score(Instructions.String(instructions), levels.map { Score.Level.String(it) })

public fun Score(instructions: List<String>, vararg levels: String): Score =
  Question.Score(Instructions.StringList(instructions), levels.map { Score.Level.String(it) })

/**
 * [T] must be [Serializable].
 */
public fun <T : Any> Score(instructions: T, vararg levels: String): Score =
  Question.Score(Instructions.Object(instructions), levels.map { Score.Level.String(it) })


// Score (Objects)

/**
 * [L] must be [Serializable].
 */
@JvmName("ScoreObjects")
public fun <L : Any> Score(instructions: String, levels: List<L>): Score =
  Question.Score(Instructions.String(instructions), levels.map { Score.Level.Object(it) })

/**
 * [L] must be [Serializable].
 */
@JvmName("ScoreObjects")
public fun <L : Any> Score(instructions: List<String>, levels: List<L>): Score =
  Question.Score(Instructions.StringList(instructions), levels.map { Score.Level.Object(it) })

/**
 * [L] and [T] must be [Serializable].
 */
@JvmName("ScoreObjects")
public fun <T : Any, L : Any> Score(instructions: T, levels: List<L>): Score =
  Question.Score(Instructions.Object(instructions), levels.map { Score.Level.Object(it) })

/**
 * [L] must be [Serializable].
 */
public fun <L : Any> Score(instructions: String, vararg levels: L): Score =
  Question.Score(Instructions.String(instructions), levels.map { Score.Level.Object(it) })

/**
 * [L] must be [Serializable].
 */
public fun <L : Any> Score(instructions: List<String>, vararg levels: L): Score =
  Question.Score(Instructions.StringList(instructions), levels.map { Score.Level.Object(it) })

/**
 * [L] and [T] must be [Serializable].
 */
public fun <T : Any, L : Any> Score(instructions: T, vararg levels: L): Score =
  Question.Score(Instructions.Object(instructions), levels.map { Score.Level.Object(it) })


// Box

public fun Box(instructions: String): Box = Question.Box(Instructions.String(instructions))
public fun Box(instructions: List<String>): Box = Question.Box(Instructions.StringList(instructions))

/**
 * [T] must be [Serializable].
 */
public fun <T : Any> Box(instructions: T): Box = Question.Box(Instructions.Object(instructions))
