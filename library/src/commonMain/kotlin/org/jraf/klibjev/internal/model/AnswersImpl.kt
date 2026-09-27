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

import org.jraf.klibjev.model.Answers
import org.jraf.klibjev.model.Question

internal data class AnswersImpl(override val answers: Map<Question, Answers.Answer>) : Answers

internal data class NoulImpl(override val value: Double) : Answers.Answer.Noul

internal data class ChoiceImpl(
  override val highestProbability: Question.Choice.Option,
  override val probabilities: Map<Question.Choice.Option, Double>,
  override val confidence: Double,
) : Answers.Answer.Choice

internal data class ScoreImpl(
  override val value: Double,
  override val probabilities: Map<String, Double>,
  override val confidence: Double,
) : Answers.Answer.Score
