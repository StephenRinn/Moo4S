
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import CowDSL.*

/**
 * Comprehensive test suite for COW DSL and Virtual Machine
 *
 * Tests cover:
 * - Individual instruction execution
 * - Memory operations
 * - Loop semantics
 * - Register operations
 * - I/O operations
 * - Complex programs
 */
class CowDSLSpec extends AnyFlatSpec with Matchers {

  // ===== Basic Instruction Tests =====

  "MoO (increment)" should "increment current cell" in {
    val output = CowDSL.cow {
      MoO
      MoO
      MoO
      OOM
    }
    output should equal("3")
  }

  "MOo (decrement)" should "decrement current cell" in {
    val output = CowDSL.cow {
      MoO
      MoO
      MoO
      MoO
      MoO
      MOo
      MOo
      OOM
    }
    output should equal("3")
  }

  "OOO (set to zero)" should "set current cell to zero" in {
    val output = CowDSL.cow {
      MoO
      MoO
      MoO
      OOO
      OOM
    }
    output should equal("0")
  }

  "mOo (move left)" should "move pointer to the left" in {
    val output = CowDSL.cow {
      MoO
      moO
      MoO
      MoO
      mOo
      OOM
    }
    output should equal("1")
  }

  // ===== Loop Tests =====

  "MOO/moo (loop)" should "execute loop body while cell is non-zero" in {
    val output = CowDSL.cow {
      MoO
      MoO
      MoO
      MOO
      MOo
      moo
      OOM
    }
    output should equal("0")
  }

  "MOO/moo (nested loops)" should "handle nested loop structures" in {
    val output = CowDSL.cow {
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MOO
      moO
      MoO
      MoO
      MoO
      MoO
      MoO
      moO
      MoO
      MoO
      MoO
      MoO
      moO
      MoO
      MoO
      MoO
      MoO
      moO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      moO
      MoO
      MoO
      MoO
      MoO
      mOo
      mOo
      mOo
      mOo
      mOo
      MOo
      moo
      moO
      moO
      moO
      moO
      Moo
      moO
      MOO
      mOo
      MoO
      moO
      MOo
      moo
      mOo
      MOo
      MOo
      MOo
      Moo
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      Moo
      Moo
      MoO
      MoO
      MoO
      Moo
      MMM
      mOo
      mOo
      mOo
      MoO
      MoO
      MoO
      MoO
      Moo
      moO
      Moo
      MOO
      moO
      moO
      MOo
      mOo
      mOo
      MOo
      moo
      moO
      moO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      Moo
      MMM
      MMM
      Moo
      MoO
      MoO
      MoO
      Moo
      MMM
      MOo
      MOo
      MOo
      Moo
      MOo
      MOo
      MOo
      MOo
      MOo
      MOo
      MOo
      MOo
      Moo
      mOo
      MoO
      Moo
    }
    output should equal("Hello, World!")
  }

  "MOO/moo (loop with counter)" should "decrement counter correctly" in {
    val output = CowDSL.cow {
      MoO
      MoO
      MoO
      MoO
      MoO
      MOO
      MOo
      moo
      OOM
    }
    output should equal("0")
  }

  // ===== Register Tests =====

  "MMM (copy)" should "copy current cell to register" in {
    val output = CowDSL.cow {
      MoO
      MoO
      MoO
      MoO
      MoO
      MMM
      OOO
      MMM
      OOM
    }
    output should equal("5")
  }

  "MMM (register toggle)" should "toggle between copy and paste modes" in {
    val output = CowDSL.cow {
      MoO
      MoO
      MoO
      MoO
      MoO
      MMM
      OOO
      moO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MMM
      OOM
    }
    output should equal("5")
  }

  // ===== I/O Tests =====

  "OOM (print integer)" should "print cell value as integer" in {
    val output = CowDSL.cow {
      MoO
      MoO
      MoO
      MoO
      MoO
      OOM
      MoO
      OOM
    }
    output should equal("56")
  }

  "OOM (multiple prints)" should "concatenate multiple integer outputs" in {
    val output = CowDSL.cow {
      MoO
      OOM
      MoO
      OOM
      MoO
      OOM
    }
    output should equal("123")
  }

  // ===== Complex Program Tests =====

  it should "D" in {
    val output = CowDSL.cow {
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      Moo
    }
    output should equal("D")
  }

  // ===== Edge Cases =====

  "Empty program" should "produce no output" in {
    val output = CowDSL.cow {
      // Empty block
    }
    output should equal("")
  }

  "Multiple cells" should "maintain independent values" in {
    val output = CowDSL.cow {
      MoO
      MoO
      MoO
      moO
      MoO
      MoO
      MoO
      MoO
      moO
      MoO
      OOM
    }
    output should equal("1")
  }

  "Zero cell check" should "skip loop when cell is zero" in {
    val output = CowDSL.cow {
      OOO
      MOO
      MoO
      moo
      MoO
      OOM
    }
    output should equal("1")
  }

  // ===== Command Accumulation Tests =====

  "cowCommands" should "return list of commands without executing" in {
    val commands = CowDSL.cowCommands {
      MoO
      MoO
      MOo
    }
    commands.length should equal(3)
    commands(0) should equal(Command.MoOSix)
    commands(1) should equal(Command.MoOSix)
    commands(2) should equal(Command.MOoFive)
  }

  // ===== Parser Tests =====

  "CowParser" should "parse moo formatted commands" in {
    val source = "MoO MoO MoO OOM"
    val output = CowInterpreter.run(source)
    output should equal("3")
  }

  "CowParser" should "ignore whitespace and comments" in {
    val source = """
      MoO
      MoO
      MoO
      OOM
    """
    val output = CowInterpreter.run(source)
    output should equal("3")
  }

  "CowParser" should "handle complex programs" in {
    val source = """
      MoO MoO MoO MoO MoO
      MOO
      MOo
      moo
      OOM
    """
    val output = CowInterpreter.run(source)
    output should equal("0")
  }

  // ===== Memory Size Tests =====

  "Custom memory size" should "allow larger programs" in {
    val output = CowDSL.cow(1000) {
      MoO
      OOM
    }
    output should equal("1")
  }

  // ===== Instruction Code Tests =====

  "Command.fromCode" should "convert codes to commands" in {
    Command.CowCommand.fromCode(0) should equal(Some(Command.mooZero))
    Command.CowCommand.fromCode(1) should equal(Some(Command.mOoOne))
    Command.CowCommand.fromCode(6) should equal(Some(Command.MoOSix))
    Command.CowCommand.fromCode(10) should equal(Some(Command.OOMTen))
    Command.CowCommand.fromCode(99) should equal(None)
  }

  // ===== Loop Matching Tests =====

  "Loop matching" should "correctly match nested loops" in {
    val output = CowDSL.cow {
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MOO
      moO
      MoO
      MoO
      MoO
      MoO
      MoO
      moO
      MoO
      MoO
      MoO
      MoO
      moO
      MoO
      MoO
      MoO
      MoO
      moO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      moO
      MoO
      MoO
      MoO
      MoO
      mOo
      mOo
      mOo
      mOo
      mOo
      MOo
      moo
      moO
      moO
      moO
      moO
      Moo
      moO
      MOO
      mOo
      MoO
      moO
      MOo
      moo
      mOo
      MOo
      MOo
      MOo
      Moo
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      Moo
      Moo
      MoO
      MoO
      MoO
      Moo
      MMM
      mOo
      mOo
      mOo
      MoO
      MoO
      MoO
      MoO
      Moo
      moO
      Moo
      MOO
      moO
      moO
      MOo
      mOo
      mOo
      MOo
      moo
      moO
      moO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      Moo
      MMM
      MMM
      Moo
      MoO
      MoO
      MoO
      Moo
      MMM
      MOo
      MOo
      MOo
      Moo
      MOo
      MOo
      MOo
      MOo
      MOo
      MOo
      MOo
      MOo
      Moo
      mOo
      MoO
      Moo
    }
    output should equal("Hello, World!")
  }

  // ===== Arithmetic Tests =====

  "Increment and decrement" should "perform basic arithmetic" in {
    val output = CowDSL.cow {
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MoO
      MOo
      MOo
      MOo
      MOo
      MOo
      OOM
    }
    output should equal("5")
  }
}
