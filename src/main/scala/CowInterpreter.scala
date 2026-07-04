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

object CowInterpreter {

  /**
   * Execute COW source code and return the output
   *
   * @param source COW source code as a string
   * @return The output produced by the program
   */
  def run(source: String): String = {
    val commands = CowParser.parseFromSource(source)
    val vm = new VirtualMachine()
    vm.execute(commands)
  }

  /**
   * Execute COW source code with custom memory size
   *
   * @param source COW source code as a string
   * @param memorySize Size of the virtual machine memory (default: 30000)
   * @return The output produced by the program
   */
  def run(source: String, memorySize: Int): String = {
    val commands = CowParser.parseFromSource(source)
    val vm = new VirtualMachine(memorySize)
    vm.execute(commands)
  }

  /**
   * Execute pre-parsed commands
   * Useful if you want to parse once and execute multiple times
   *
   * @param commands List of parsed COW commands
   * @return The output produced by the program
   */
  def execute(commands: List[Command.CowCommand]): String = {
    val vm = new VirtualMachine()
    vm.execute(commands)
  }

  /**
   * Execute pre-parsed commands with custom memory size
   *
   * @param commands List of parsed COW commands
   * @param memorySize Size of the virtual machine memory (default: 30000)
   * @return The output produced by the program
   */
  def execute(commands: List[Command.CowCommand], memorySize: Int): String = {
    val vm = new VirtualMachine(memorySize)
    vm.execute(commands)
  }

  /**
   * Parse COW source code without executing it
   * Useful for validation or analysis
   *
   * @param source COW source code as a string
   * @return List of parsed commands
   */
  def parse(source: String): List[Command.CowCommand] = {
    CowParser.parseFromSource(source)
  }

  /**
   * Tokenize COW source code without parsing or executing
   * Useful for debugging the lexer
   *
   * @param source COW source code as a string
   * @return List of tokens
   */
  def tokenize(source: String): List[CowToken] = {
    CowLexer.tokenize(source)
  }

  /**
   * Get detailed information about a program
   *
   * @param source COW source code as a string
   * @return ProgramInfo containing tokens, commands, and metadata
   */
  def analyze(source: String): ProgramInfo = {
    val tokens = CowLexer.tokenize(source)
    val commands = CowParser.parse(tokens)
    ProgramInfo(
      sourceLength = source.length,
      tokenCount = tokens.length,
      commandCount = commands.length,
      tokens = tokens,
      commands = commands
    )
  }
}

/**
 * Information about a parsed COW program
 */
case class ProgramInfo(
                        sourceLength: Int,
                        tokenCount: Int,
                        commandCount: Int,
                        tokens: List[CowToken],
                        commands: List[Command.CowCommand]
                      )
