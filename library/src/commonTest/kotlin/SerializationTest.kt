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

package org.jraf.klibjev

import com.apollographql.mockserver.MockServer
import com.apollographql.mockserver.enqueueError
import com.apollographql.mockserver.enqueueString
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import okio.use
import org.jraf.klibjev.client.JevClient
import org.jraf.klibjev.client.JevClient.Configuration
import org.jraf.klibjev.internal.model.AnswersImpl
import org.jraf.klibjev.internal.model.ChoiceImpl
import org.jraf.klibjev.internal.model.NoulImpl
import org.jraf.klibjev.internal.model.ScoreImpl
import org.jraf.klibjev.model.Question
import org.jraf.klibjev.model.State
import kotlin.test.Test
import kotlin.test.assertEquals

class SerializationTest {
  @Test
  fun `string state serializes correctly`() = runTest {
    MockServer().apply { enqueueError(500) }.use { mockServer ->
      JevClient(Configuration(apiKey = "xxx", http = Configuration.Http(mockServer.url()))).use { jevClient ->
        jevClient.evaluate(
          state = State("Help! My payouts have been failing for 3 days."),
          questions = setOf(Question.Noul(instructions = "Does this convey urgency?")),
        )
        assertEquals(
          expected = """{"model":"jev-latest","state":"Help! My payouts have been failing for 3 days.","questions":{"q0":{"type":"noul","instructions":"Does this convey urgency?"}}}""",
          actual = mockServer.takeRequest().body.utf8(),
        )
      }
    }
  }

  @Test
  fun `string list state serializes correctly`() = runTest {
    MockServer().apply { enqueueError(500) }.use { mockServer ->
      JevClient(Configuration(apiKey = "xxx", http = Configuration.Http(mockServer.url()))).use { jevClient ->
        jevClient.evaluate(
          state = State(listOf("Help! My payouts have been failing for 3 days.")),
          questions = setOf(Question.Noul(instructions = "Does this convey urgency?")),
        )
        assertEquals(
          expected = """{"model":"jev-latest","state":["Help! My payouts have been failing for 3 days."],"questions":{"q0":{"type":"noul","instructions":"Does this convey urgency?"}}}""",
          actual = mockServer.takeRequest().body.utf8(),
        )
      }
    }
  }

  @Test
  fun `object state serializes correctly`() = runTest {
    MockServer().apply { enqueueError(500) }.use { mockServer ->
      JevClient(Configuration(apiKey = "xxx", http = Configuration.Http(mockServer.url()))).use { jevClient ->
        val state = State(
          MyState(
            ticket = MyState.Ticket(
              subject = "Duplicate charge",
              messages = listOf(
                MyState.Ticket.Message(from = "customer", text = "I was charged twice for order A-104. Please refund the duplicate."),
                MyState.Ticket.Message(from = "support", text = "We are checking the charges."),
              ),
            ),
            order = MyState.Order(
              id = "A-104",
              charges = listOf(
                MyState.Order.Charge(amount_usd = 49, status = "captured"),
                MyState.Order.Charge(amount_usd = 49, status = "captured"),
              ),
            ),
            refund_policy = "Duplicate charges are eligible for a refund.",
          ),
        )

        jevClient.evaluate(
          state = state,
          questions = setOf(Question.Noul(instructions = "Is the customer asking for a refund?")),
        )
        assertEquals(
          expected = """{"model":"jev-latest","state":{"ticket":{"subject":"Duplicate charge","messages":[{"from":"customer","text":"I was charged twice for order A-104. Please refund the duplicate."},{"from":"support","text":"We are checking the charges."}]},"order":{"id":"A-104","charges":[{"amount_usd":49,"status":"captured"},{"amount_usd":49,"status":"captured"}]},"refund_policy":"Duplicate charges are eligible for a refund."},"questions":{"q0":{"type":"noul","instructions":"Is the customer asking for a refund?"}}}""",
          actual = mockServer.takeRequest().body.utf8(),
        )
      }
    }
  }

  @Test
  fun `several questions serialize correctly`() = runTest {
    MockServer().use { mockServer ->
      mockServer.enqueueString(
        contentType = "application/json",
        string =
          // language=JSON
          """
          {
            "model": "jev-1.13.0",
            "answers": {
              "q0": {
                "type": "noul",
                "noul": 0.99
              },
              "q1": {
                "type": "choice",
                "choice": "French",
                "confidence": 1.0,
                "probabilities": {
                  "Spanish": 0.0,
                  "English": 0.0,
                  "French": 1.0
                }
              },
              "q2": {
                "type": "score",
                "score": 2.65,
                "confidence": 0.69,
                "legend": {
                  "0": "Very cold",
                  "1": "Cold",
                  "2": "Neutral",
                  "3": "Warm",
                  "4": "Very warm"
                },
                "probabilities": {
                  "0": 0.0,
                  "1": 0.0,
                  "2": 0.35,
                  "3": 0.64,
                  "4": 0.01
                }
              }
            },
            "usage": {
              "input_tokens": 361,
              "output_tokens": 71
            }
          }
        """.trimIndent(),
      )

      JevClient(Configuration(apiKey = "xxx", http = Configuration.Http(mockServer.url()))).use { jevClient ->
        val answers = jevClient.evaluate(
          state = State("Help! My payouts have been failing for 3 days."),
          questions = setOf(
            Question.Noul(instructions = "Does this convey urgency?"),
            Question.Choice(
              instructions = "What is the language of this text?",
              options = setOf(Question.Choice.Option("English"), Question.Choice.Option("French"), Question.Choice.Option("Spanish")),
            ),
            Question.Score(
              instructions = "How warm is this text?",
              levels = listOf("Very cold", "Cold", "Neutral", "Warm", "Very warm"),
            ),
          ),
        )
        assertEquals(
          expected = """{"model":"jev-latest","state":"Help! My payouts have been failing for 3 days.","questions":{"q0":{"type":"noul","instructions":"Does this convey urgency?"},"q1":{"type":"choice","instructions":"What is the language of this text?","criteria":{"English":null,"French":null,"Spanish":null}},"q2":{"type":"score","instructions":"How warm is this text?","criteria":["Very cold","Cold","Neutral","Warm","Very warm"]}}}""",
          actual = mockServer.takeRequest().body.utf8(),
        )
        assertEquals(
          expected = AnswersImpl(
            answers = mapOf(
              Question.Noul(instructions = "Does this convey urgency?") to NoulImpl(value = 0.99),
              Question.Choice(
                instructions = "What is the language of this text?",
                options = setOf(Question.Choice.Option("English"), Question.Choice.Option("French"), Question.Choice.Option("Spanish")),
              ) to ChoiceImpl(
                highestProbability = Question.Choice.Option("French"),
                probabilities = mapOf(
                  Question.Choice.Option("Spanish") to 0.0,
                  Question.Choice.Option("English") to 0.0,
                  Question.Choice.Option("French") to 1.0,
                ),
                confidence = 1.0,
              ),
              Question.Score(
                instructions = "How warm is this text?",
                levels = listOf("Very cold", "Cold", "Neutral", "Warm", "Very warm"),
              ) to ScoreImpl(
                value = 2.65,
                probabilities = mapOf(
                  "Very cold" to 0.0,
                  "Cold" to 0.0,
                  "Neutral" to 0.35,
                  "Warm" to 0.64,
                  "Very warm" to 0.01,
                ),
                confidence = 0.69,
              ),
            ),
          ),
          actual = answers.getOrThrow(),
        )
      }
    }
  }


  @Serializable
  class MyState(
    val ticket: Ticket,
    val order: Order,
    val refund_policy: String,
  ) {
    @Serializable
    class Ticket(
      val subject: String,
      val messages: List<Message>,
    ) {
      @Serializable
      class Message(
        val from: String,
        val text: String,
      )
    }

    @Serializable
    class Order(
      val id: String,
      val charges: List<Charge>,
    ) {
      @Serializable
      class Charge(
        val amount_usd: Int,
        val status: String,
      )
    }
  }
}
