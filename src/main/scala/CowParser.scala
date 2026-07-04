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

object CowParser {

  /**
   * Parse a list of tokens into a list of commands
   *
   * @param tokens List of CowTokens from the lexer
   * @return List of Command.CowCommand objects ready for execution
   */
  def parse(tokens: List[CowToken]): List[Command.CowCommand] = {
    tokens.flatMap(tokenToCommand)
  }

  /**
   * Convert a single token to its corresponding command
   *
   * @param token A CowToken from the lexer
   * @return Option containing the corresponding Command.CowCommand, or None if unknown
   */
  private def tokenToCommand(token: CowToken): Option[Command.CowCommand] = {
    token match {
      case MooTokenZero => Some(Command.mooZero)
      case MOoTokenOne => Some(Command.mOoOne)
      case MoOTokenTwo => Some(Command.moOTwo)
      case MOOTokenThree => Some(Command.mOOThree)
      case MooInstrTokenFour => Some(Command.MooFour)
      case MOoInstrTokenFive => Some(Command.MOoFive)
      case MoOInstrTokenSix => Some(Command.MoOSix)
      case MOOInstrTokenSeven => Some(Command.MOOSeven)
      case OOOTokenEight => Some(Command.OOOEight)
      case MMMTokenNine => Some(Command.MMMNine)
      case OOMTokenTen => Some(Command.OOMTen)
      case OomTokenEleven => Some(Command.oomEleven)
      case UnknownToken(_) => None
    }
  }

  /**
   * Convenience method: parse directly from source code string
   * Combines lexing and parsing in one step
   *
   * @param source COW source code as a string
   * @return List of Command.CowCommand objects ready for execution
   */
  def parseFromSource(source: String): List[Command.CowCommand] = {
    val tokens = CowLexer.tokenize(source)
    parse(tokens)
  }
}
