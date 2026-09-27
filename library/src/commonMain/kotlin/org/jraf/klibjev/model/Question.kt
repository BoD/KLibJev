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

sealed interface Question {
  val instructions: String

  @Poko
  class Noul(
    override val instructions: String,
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
    override val instructions: String,

    /**
     * Min: 2, max: 255.
     */
    val options: Set<Option>,
  ) : Question {
    @Poko
    class Option(
      val name: String,
      val description: String? = null,
    )
  }

  @Poko
  class Score(
    override val instructions: String,

    /**
     * Min: 2, max: 10.
     */
    val levels: List<String>,
  ) : Question
}
