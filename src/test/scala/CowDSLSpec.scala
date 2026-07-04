import Command.CowCommand
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import CowDSL.*

class CowDSLSpec extends AnyFlatSpec with Matchers {

  "CowDSL" should "execute a single instruction" in {
    val result = cow {
      Moo
    }
    result should be("")
  }

  it should "execute multiple instructions in sequence" in {
    val result = cow {
      Moo
      moo
      MOo
      MoO
    }
    result should be("")
  }

  it should "accumulate commands in order" in {
    val commands = cowCommands {
      Moo
      moo
      MOo
      MoO
    }
    commands should equal(List(
      MooInstrTokenFour,
      MooTokenZero,
      MOoTokenOne,
      MoOTokenTwo
    ))
  }

  it should "execute all 12 instructions" in {
    val commands = cowCommands {
      moo
      mOo
      moO
      mOO
      Moo
      MOo
      MoO
      MOO
      OOO
      MMM
      OOM
      oom
    }
    commands.length should equal(12)
    commands should equal(List(
      MooTokenZero,
      MOoTokenOne,
      MoOTokenTwo,
      MOOTokenThree,
      MooInstrTokenFour,
      MOoInstrTokenFive,
      MoOInstrTokenSix,
      MOOInstrTokenSeven,
      OOOTokenEight,
      MMMTokenNine,
      OOMTokenTen,
      OomTokenEleven
    ))
  }

  it should "handle empty block" in {
    val result = cow {
      // empty
    }
    result should be("")
  }

  it should "handle repeated instructions" in {
    val commands = cowCommands {
      Moo
      Moo
      Moo
      moo
      moo
    }
    commands should equal(List(
      MooInstrTokenFour,
      MooInstrTokenFour,
      MooInstrTokenFour,
      MooTokenZero,
      MooTokenZero
    ))
  }

  it should "handle alternating instructions" in {
    val commands = cowCommands {
      Moo
      moo
      Moo
      moo
      Moo
    }
    commands should equal(List(
      MooInstrTokenFour,
      MooTokenZero,
      MooInstrTokenFour,
      MooTokenZero,
      MooInstrTokenFour
    ))
  }

  it should "handle loop structures" in {
    val commands = cowCommands {
      MOO
      Moo
      moo
    }
    commands should equal(List(
      MOOInstrTokenSeven,
      MooInstrTokenFour,
      MooTokenZero
    ))
  }

  it should "handle nested loops" in {
    val commands = cowCommands {
      MOO
      MOO
      Moo
      moo
      moo
    }
    commands should equal(List(
      MOOInstrTokenSeven,
      MOOInstrTokenSeven,
      MooInstrTokenFour,
      MooTokenZero,
      MooTokenZero
    ))
  }

  it should "handle complex instruction sequences" in {
    val commands = cowCommands {
      MoO
      moO
      MoO
      moO
      MOO
      OOM
      MMM
      moO
      moO
      MMM
      mOo
      mOo
      moO
      MMM
      mOo
      MMM
      moO
      moO
      MOO
      MOo
      mOo
      MoO
      moO
      moo
      mOo
      mOo
      moo
    }
    commands.length should equal(27)
  }

  it should "support cowCommands without executing" in {
    val commands = cowCommands {
      Moo
      moo
      MOo
    }
    commands should equal(List(
      MooInstrTokenFour,
      MooTokenZero,
      MOoTokenOne
    ))
  }

  it should "execute with default memory size" in {
    val result = cow {
      MoO
      OOM
    }
    result should be("1")
  }

  it should "execute with custom memory size" in {
    val result = cow(5000) {
      MoO
      OOM
    }
    result should be("1")
  }

  it should "handle multiple cow blocks independently" in {
    val result1 = cow {
      MoO
      OOM
    }
    val result2 = cow {
      MoO
      MoO
      OOM
    }
    result1 should equal("1")
    result2 should equal("2")
  }

  it should "handle nested cow blocks" in {
    val innerResult = cow {
      MoO
      OOM
    }
    val outerResult = cow {
      MoO
      MoO
      OOM
    }
    innerResult should equal("1")
    outerResult should equal("2")
  }

  it should "clear buffer between executions" in {
    val result1 = cow {
      MoO
      OOM
    }
    val result2 = cow {
      MoO
      MoO
      MoO
      OOM
    }
    result1 should equal("1")
    result2 should equal("3")
  }

  it should "handle long instruction sequences" in {
    val commands = cowCommands {
      for (_ <- 1 to 100) {
        MoO
      }
    }
    commands.length should equal(100)
    commands.forall(_ == MoOInstrTokenSix) should be(true)
  }

  it should "handle all instruction types in one block" in {
    val commands = cowCommands {
      moo
      mOo
      moO
      mOO
      Moo
      MOo
      MoO
      MOO
      OOO
      MMM
      OOM
      oom
    }
    commands.length should equal(12)
  }

  it should "preserve instruction order with mixed types" in {
    val commands = cowCommands {
      OOM
      MMM
      OOO
      MOO
      MoO
      MOo
      Moo
      mOO
      moO
      mOo
      moo
    }
    commands should equal(List(
      OOMTokenTen,
      MMMTokenNine,
      OOOTokenEight,
      MOOInstrTokenSeven,
      MoOInstrTokenSix,
      MOoInstrTokenFive,
      MooInstrTokenFour,
      MOOTokenThree,
      MoOTokenTwo,
      MOoTokenOne,
      MooTokenZero
    ))
  }

  it should "handle increment and decrement sequences" in {
    val commands = cowCommands {
      MoO
      MoO
      MoO
      MOo
      MOo
    }
    commands should equal(List(
      MoOInstrTokenSix,
      MoOInstrTokenSix,
      MoOInstrTokenSix,
      MOoInstrTokenFive,
      MOoInstrTokenFive
    ))
  }

  it should "handle pointer movement sequences" in {
    val commands = cowCommands {
      moO
      moO
      mOo
      mOo
      moO
    }
    commands should equal(List(
      MoOTokenTwo,
      MoOTokenTwo,
      MOoTokenOne,
      MOoTokenOne,
      MoOTokenTwo
    ))
  }

  it should "handle I/O sequences" in {
    val commands = cowCommands {
      Moo
      OOM
      oom
      Moo
    }
    commands should equal(List(
      MooInstrTokenFour,
      OOMTokenTen,
      OomTokenEleven,
      MooInstrTokenFour
    ))
  }

  it should "handle control flow sequences" in {
    val commands = cowCommands {
      MOO
      Moo
      moo
      MOO
      Moo
      moo
    }
    commands should equal(List(
      MOOInstrTokenSeven,
      MooInstrTokenFour,
      MooTokenZero,
      MOOInstrTokenSeven,
      MooInstrTokenFour,
      MooTokenZero
    ))
  }

  it should "handle special operations" in {
    val commands = cowCommands {
      OOO
      MMM
      mOO
    }
    commands should equal(List(
      OOOTokenEight,
      MMMTokenNine,
      MOOTokenThree
    ))
  }

}

/**
 * Tests for CowProgram trait
 */
class CowProgramSpec extends AnyFlatSpec with Matchers {

  class TestProgram extends CowProgram {
    def simpleProgram(): String = {
      executeBlock {
        Moo
        moo
        MOo
      }
    }

    def incrementProgram(): String = {
      executeBlock {
        MoO
        OOM
      }
    }

    def multiIncrementProgram(): String = {
      executeBlock {
        MoO
        MoO
        MoO
        OOM
      }
    }

    def customMemoryProgram(): String = {
      executeBlock(5000) {
        MoO
        OOM
      }
    }

    def getCommandsExample(): List[CowCommand] = {
      getCommands {
        Moo
        moo
        MOo
        MoO
      }
    }

    def loopProgram(): String = {
      executeBlock {
        MOO
        Moo
        moo
      }
    }

    def allInstructions(): String = {
      executeBlock {
        moo
        mOo
        moO
        mOO
        Moo
        MOo
        MoO
        MOO
        OOO
        MMM
        OOM
        oom
      }
    }
  }

  "CowProgram" should "execute a simple program" in {
    val program = new TestProgram()
    val result = program.simpleProgram()
    result should be("")
  }

  it should "execute an increment program" in {
    val program = new TestProgram()
    val result = program.incrementProgram()
    result should equal("1")
  }

  it should "execute a multi-increment program" in {
    val program = new TestProgram()
    val result = program.multiIncrementProgram()
    result should equal("3")
  }

  it should "execute with custom memory size" in {
    val program = new TestProgram()
    val result = program.customMemoryProgram()
    result should equal("1")
  }

  it should "get commands without executing" in {
    val program = new TestProgram()
    val commands = program.getCommandsExample()
    commands should equal(List(
      MooInstrTokenFour,
      MooTokenZero,
      MOoTokenOne,
      MoOTokenTwo
    ))
  }

  it should "execute a loop program" in {
    val program = new TestProgram()
    val result = program.loopProgram()
    result should be("")
  }

  it should "execute all instructions" in {
    val program = new TestProgram()
    val result = program.allInstructions()
    result should be("")
  }

  it should "support multiple method calls" in {
    val program = new TestProgram()
    val result1 = program.incrementProgram()
    val result2 = program.multiIncrementProgram()
    result1 should equal("1")
    result2 should equal("3")
  }

  it should "maintain independent state between calls" in {
    val program = new TestProgram()
    val result1 = program.incrementProgram()
    val result2 = program.incrementProgram()
    result1 should equal("1")
    result2 should equal("1")
  }

}
