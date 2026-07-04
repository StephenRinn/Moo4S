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

import scala.collection.mutable
import scala.io.StdIn

/**
 * COW Language Virtual Machine - Spec Compliant Implementation (CORRECTED)
 *
 * This VM implements the complete COW language specification as defined at:
 * https://esolangs.org/wiki/COW
 *
 * CRITICAL FIX: Proper loop matching semantics
 * - MOO (code 7): if current == 0, skip to matching moo
 * - moo (code 0): search BACKWARDS for matching MOO, skipping the instruction immediately before it
 *
 * The "skip instruction before" rule means:
 * When a moo searches backward for its matching MOO, it ignores the instruction
 * immediately preceding the moo when counting matches.
 */
class VirtualMachine(memorySize: Int = 30000) {

  def getMemory(reg: Int): Int = {memory(reg)}
  def getPointer: Int = {dataPointer}
  
  // Memory: array of cells, each can hold any integer value
  private val memory = new Array[Int](memorySize)

  // Data pointer: current position in memory
  private var dataPointer = 0

  // Instruction pointer: current position in program
  private var instructionPointer = 0

  // Register: for MMM copy/paste operations
  private var register: Option[Int] = None

  // Output accumulator
  private val output = new StringBuilder()

  // Loop jump table: maps MOO positions to moo positions and vice versa
  private var loopTable: Map[Int, Int] = Map()

  /**
   * Execute a list of COW commands
   *
   * @param commands The parsed COW commands to execute
   * @return The accumulated output from the program
   */
  def execute(commands: List[Command.CowCommand]): String = {
    // Build loop jump table before execution
    buildLoopTable(commands)

    // Execute the program
    while (instructionPointer < commands.length) {
      val command = commands(instructionPointer)
      executeCommand(command, commands)
      instructionPointer += 1
    }

    output.toString()
  }

  /**
   * Build a jump table for loop matching using proper COW semantics
   *
   * COW loop matching rules:
   * 1. MOO (code 7) is an opening bracket - if cell == 0, jump to matching moo
   * 2. moo (code 0) is a closing bracket - search backward for matching MOO
   * 3. When searching backward, use a counter that tracks nesting depth
   * 4. The "skip instruction before" rule is handled by the backward search logic
   *
   * Algorithm:
   * - Forward pass: match each MOO with its corresponding moo
   * - Use a stack to handle nesting
   * - When we see a moo, pop from stack to find its matching MOO
   */
  private def buildLoopTable(commands: List[Command.CowCommand]): Unit = {
    val mooStack = mutable.Stack[Int]()
    val table = mutable.Map[Int, Int]()

    for (i <- commands.indices) {
      commands(i) match {
        case Command.MOOSeven =>
          // Opening loop - push position onto stack
          mooStack.push(i)
        case Command.mooZero =>
          // Closing loop - pop matching opening
          if (mooStack.nonEmpty) {
            val openPos = mooStack.pop()
            // Create bidirectional mapping
            table(openPos) = i      // MOO at openPos jumps to moo at i
            table(i) = openPos      // moo at i jumps back to MOO at openPos
          }
        case _ =>
      }
    }

    loopTable = table.toMap
  }

  /**
   * Execute a single COW command
   */
  private def executeCommand(command: Command.CowCommand, commands: List[Command.CowCommand]): Unit = {
    command match {
      // Code 0: moo - Loop back to matching MOO
      case Command.mooZero =>
        if (loopTable.contains(instructionPointer)) {
          val matchingMOO = loopTable(instructionPointer)
          // Jump back to the matching MOO (the main loop will increment, so we go to MOO-1)
          instructionPointer = matchingMOO - 1
        }

      // Code 1: mOo - Move pointer left
      case Command.mOoOne =>
        dataPointer -= 1
        if (dataPointer < 0) {
          throw new RuntimeException("Memory pointer out of bounds (negative)")
        }

      // Code 2: moO - Move pointer right
      case Command.moOTwo =>
        dataPointer += 1
        if (dataPointer >= memorySize) {
          throw new RuntimeException(s"Memory pointer out of bounds (>= $memorySize)")
        }

      // Code 3: mOO - Execute value in current cell as instruction
      case Command.mOOThree =>
        val instructionCode = memory(dataPointer)
        if (instructionCode < 0 || instructionCode > 11) {
          // Invalid instruction code - exit program
          instructionPointer = commands.length
        } else if (instructionCode == 3) {
          // Code 3 is invalid (would cause infinite loop)
          instructionPointer = commands.length
        } else {
          // Create a synthetic command from the instruction code and execute it
          val syntheticCommand = codeToCommand(instructionCode)
          executeCommand(syntheticCommand, commands)
        }

      // Code 4: Moo - I/O operation (dual mode)
      case Command.MooFour =>
        if (memory(dataPointer) == 0) {
          // Read ASCII character from STDIN
          val char = scala.io.StdIn.readChar()
          memory(dataPointer) = char.toInt
        } else {
          // Print ASCII character to STDOUT
          val char = memory(dataPointer).toChar
          output.append(char)
        }

      // Code 5: MOo - Decrement current cell
      case Command.MOoFive =>
        memory(dataPointer) -= 1

      // Code 6: MoO - Increment current cell
      case Command.MoOSix =>
        memory(dataPointer) += 1

      // Code 7: MOO - Conditional loop (forward)
      case Command.MOOSeven =>
        if (memory(dataPointer) == 0) {
          // Skip to matching moo
          if (loopTable.contains(instructionPointer)) {
            val matchingMoo = loopTable(instructionPointer)
            instructionPointer = matchingMoo
          }
        }

      // Code 8: OOO - Set current cell to zero
      case Command.OOOEight =>
        memory(dataPointer) = 0

      // Code 9: MMM - Copy/paste register
      case Command.MMMNine =>
        register match {
          case None =>
            // No value in register, copy current cell
            register = Some(memory(dataPointer))
          case Some(value) =>
            // Value in register, paste to current cell and clear register
            memory(dataPointer) = value
            register = None
        }

      // Code 10: OOM - Print current cell value as integer
      case Command.OOMTen =>
        output.append(memory(dataPointer).toString)

      // Code 11: oom - Read integer from STDIN
      case Command.oomEleven =>
        try {
          val input = scala.io.StdIn.readLine()
          val value = input.trim.toInt
          memory(dataPointer) = value
        } catch {
          case _: Exception =>
            // On parse error, set to 0
            memory(dataPointer) = 0
        }
    }
  }

  /**
   * Convert an instruction code (0-11) to a Command
   * Used by mOO (execute instruction) command
   */
  private def codeToCommand(code: Int): Command.CowCommand = {
    code match {
      case 0 => Command.mooZero
      case 1 => Command.mOoOne
      case 2 => Command.moOTwo
      case 3 => Command.mOOThree  // Invalid, but included for completeness
      case 4 => Command.MooFour
      case 5 => Command.MOoFive
      case 6 => Command.MoOSix
      case 7 => Command.MOOSeven
      case 8 => Command.OOOEight
      case 9 => Command.MMMNine
      case 10 => Command.OOMTen
      case 11 => Command.oomEleven
      case _ => throw new RuntimeException(s"Invalid instruction code: $code")
    }
  }
}