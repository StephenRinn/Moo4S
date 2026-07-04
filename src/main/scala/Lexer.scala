/*
 * Copyright 2026 Stephen Rinn
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

sealed trait CowToken

case object MooTokenZero extends CowToken         // 0: Loop back
case object MOoTokenOne extends CowToken          // 1: Move pointer left
case object MoOTokenTwo extends CowToken          // 2: Move pointer right
case object MOOTokenThree extends CowToken        // 3: Execute value as instruction
case object MooInstrTokenFour extends CowToken    // 4: I/O (read/print)
case object MOoInstrTokenFive extends CowToken    // 5: Decrement
case object MoOInstrTokenSix extends CowToken     // 6: Increment
case object MOOInstrTokenSeven extends CowToken   // 7: Loop
case object OOOTokenEight extends CowToken        // 8: Set to 0
case object MMMTokenNine extends CowToken         // 9: Copy/paste register
case object OOMTokenTen extends CowToken          // 10: Print as integer
case object OomTokenEleven extends CowToken       // 11: Read integer

case class UnknownToken(value: String) extends CowToken

object CowLexer {
  private val threeCharTokens: Map[String, CowToken] = Map(
    "moo" -> MooTokenZero,
    "mOo" -> MOoTokenOne,
    "moO" -> MoOTokenTwo,
    "mOO" -> MOOTokenThree,
    "Moo" -> MooInstrTokenFour,
    "MOo" -> MOoInstrTokenFive,
    "MoO" -> MoOInstrTokenSix,
    "MOO" -> MOOInstrTokenSeven,
    "OOO" -> OOOTokenEight,
    "MMM" -> MMMTokenNine,
    "OOM" -> OOMTokenTen,
    "oom" -> OomTokenEleven
  )

  private def matchInstruction(source: String, startIndex: Int): Option[(CowToken, Int)] = {
    Option.when(startIndex + 2 < source.length) {
      source.substring(startIndex, startIndex + 3)
    }.flatMap(threeCharTokens.get(_).map(_ -> 3))
  }

  def tokenize(source: String): List[CowToken] = {
    @scala.annotation.tailrec
    def loop(index: Int, acc: List[CowToken]): List[CowToken] = {
      if (index >= source.length)
        acc.reverse
      else
        matchInstruction(source, index) match {
          case Some((token, length)) =>
            loop(index + 3, token :: acc)

          case None =>
            loop(index + 1, acc)
        }
    }
    loop(0, Nil)
  }
}