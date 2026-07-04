import Command.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers


class CowFunctionalSpec extends AnyFlatSpec with Matchers {


  it should "mutate memory with MoO and MOo correctly" in {
    val vm = new VirtualMachine()
    vm.execute(List(
      MoOSix, MoOSix, MoOSix,   // +3
      MOoFive              // -1
    ))

    vm.getMemory(0) shouldBe 2
  }

  it should "move pointer left and right correctly" in {
    val vm = new VirtualMachine()

    vm.execute(List(
      moOTwo, moOTwo, // right twice
      MoOSix, // mem[2] = 1
      mOoOne, mOoOne, // back to 0
      MoOSix // mem[0] = 1
    ))

    vm.getMemory(0) shouldBe 1
    vm.getMemory(2) shouldBe 1
  }

  it should "copy register then restore value into memory (not swap)" in {
    val vm = new VirtualMachine()

    vm.execute(List(
      MoOSix, MoOSix, MoOSix, // mem[0] = 3
      MMMNine,                 // register = 3
      MoOSix, MoOSix, MoOSix, // mem[0] = 6
      MMMNine                  // restore register into mem[0]
    ))

    vm.getMemory(0) shouldBe 3
  }

  it should "output integer using OOM" in {
    val vm = new VirtualMachine()

    val out = vm.execute(List(
      MoOSix, MoOSix, MoOSix,
      OOMTen
    ))

    out shouldBe "3"
  }

  it should "output character when memory non-zero" in {
    val vm = new VirtualMachine()

    val out = vm.execute(List(
      MoOSix, MoOSix, MoOSix,
      MooFour
    ))

    out shouldBe "" // ASCII 3
  }

  it should "reduce counter to zero using loop" in {
    val vm = new VirtualMachine()

    vm.execute(List(
      MoOSix,   // +1
      MoOSix,   // +1
      MoOSix,   // +1  → memory[0] = 3
      MOOSeven, // loop open
      MOoFive,  // -1
      mooZero   // loop close → back to MOO
    ))

    vm.getMemory(0) shouldBe 0
  }

  it should "handle nested loops correctly" in {
    val vm = new VirtualMachine()

    vm.execute(List(
      MoOSix,
      MoOSix,
      MOOSeven,
      moOTwo,
      MoOSix,
      MoOSix,
      MoOSix,
      MOOSeven,
      MOoFive,
      mooZero,
      mOoOne,
      MOoFive,
      mooZero,
      mOoOne
    ))

    vm.getMemory(0) shouldBe 0

    vm.getMemory(0) shouldBe 0
  }

  it should "exit loop only when condition is met" in {
    val vm = new VirtualMachine()

    vm.execute(List(
      MoOSix, MoOSix,
      MOOSeven,
      MOoFive, MOoFive,
      MOOSeven,
      MoOSix
    ))

    vm.getMemory(0) shouldBe 0
  }

  it should "make MMM reversible" in {
    val vm = new VirtualMachine()

    vm.execute(List(
      MoOSix, MoOSix, MoOSix,
      MMMNine,
      MoOSix, MoOSix,
      MMMNine
    ))

    vm.getMemory(0) shouldBe 3
  }

  it should "be deterministic across runs" in {
    val vm1 = new VirtualMachine()
    val vm2 = new VirtualMachine()

    val program = List(
      MoOSix, MoOSix, MoOSix,
      MOoFive,
      OOMTen
    )

    val r1 = vm1.execute(program)
    val r2 = vm2.execute(program)

    r1 shouldBe r2
  }

  it should "zero memory cell correctly" in {
    val vm = new VirtualMachine()

    vm.execute(List(
      MoOSix, MoOSix, MoOSix,
      OOOEight
    ))

    vm.getMemory(0) shouldBe 0
  }
}
