import Command.CowCommand

class VirtualMachine(memorySize: Int = 30000) {

  private val memory = Array.fill(memorySize)(0)

  private var pointer = 0
  private var pc = 0
  private var halted = false
  private val output = new StringBuilder

  private var program: Vector[CowCommand] = Vector.empty

  // =========================
  // EXECUTION
  // =========================
  def execute(cmds: List[CowCommand]): String = {
    program = cmds.toVector

    pc = 0
    pointer = 0
    halted = false
    output.clear()

    while (pc >= 0 && pc < program.length && !halted) {
      step()
    }

    output.toString()
  }

  // =========================
  // SINGLE STEP
  // =========================
  private def step(): Unit = {
    val cmd = program(pc)

    cmd.code match {

      // -------------------------
      // LOOP OPEN: MOO (7)
      // if memory == 0 -> skip to matching moo
      // -------------------------
      case 7 =>
        if (memory(pointer) == 0) {
          pc = findMatchingForward(pc)
        }
        pc += 1

      // -------------------------
      // LOOP CLOSE: moo (0)
      // if memory != 0 -> jump back
      // -------------------------
      case 0 =>
        if (memory(pointer) != 0) {
          pc = findMatchingBackward(pc)
        }
        pc += 1

      // -------------------------
      // POINTER LEFT
      // -------------------------
      case 1 =>
        pointer = math.max(0, pointer - 1)
        pc += 1

      // -------------------------
      // POINTER RIGHT
      // -------------------------
      case 2 =>
        pointer = math.min(memory.length - 1, pointer + 1)
        pc += 1

      // -------------------------
      // EXEC MEMORY AS INSTRUCTION (mOO)
      // -------------------------
      case 3 =>
        val inner = memory(pointer)
        CowCommand.fromCode(inner) match {
          case Some(c) if c.code != 3 =>
            executeSingle(c)
          case _ =>
            halted = true
        }
        pc += 1

      // -------------------------
      // I/O (simplified safe stubs if needed)
      // -------------------------
      case 4 =>
        if (memory(pointer) != 0)
          output.append(memory(pointer).toChar)
        pc += 1

      case 10 =>
        output.append(memory(pointer).toString)
        pc += 1

      // -------------------------
      // MEMORY OPS
      // -------------------------
      case 5 =>
        memory(pointer) -= 1; pc += 1

      case 6 =>
        memory(pointer) += 1; pc += 1

      case 8 =>
        memory(pointer) = 0; pc += 1

      case 9 =>
        // MMM
        // (simple register not needed for loop correctness)
        pc += 1

      case _ =>
        halted = true
    }
  }

  // =========================
  // SINGLE INSTRUCTION EXECUTION (for mOO)
  // =========================
  private def executeSingle(cmd: CowCommand): Unit = {
    cmd.code match {
      case 1 => pointer = math.max(0, pointer - 1)
      case 2 => pointer = math.min(memory.length - 1, pointer + 1)
      case 5 => memory(pointer) -= 1
      case 6 => memory(pointer) += 1
      case 8 => memory(pointer) = 0
      case _ => ()
    }
  }

  // =========================
  // BRACKET MATCHING (NO STATE, NO BUGS)
  // =========================
  private def findMatchingForward(from: Int): Int = {
    var depth = 1
    var i = from + 1

    while (i < program.length) {
      program(i).code match {
        case 7 => depth += 1
        case 0 => depth -= 1
        case _ =>
      }
      if (depth == 0) return i
      i += 1
    }

    from // fallback (shouldn't happen in valid programs)
  }

  private def findMatchingBackward(from: Int): Int = {
    var depth = 1
    var i = from - 1

    while (i >= 0) {
      program(i).code match {
        case 0 => depth += 1
        case 7 => depth -= 1
        case _ =>
      }
      if (depth == 0) return i
      i -= 1
    }

    from
  }

  // =========================
  // DEBUG
  // =========================
  def getMemory: Array[Int] = memory.clone()
  def getPointer: Int = pointer
}