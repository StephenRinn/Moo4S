import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Command._
import Command.CowCommand._

class CowLoopSpec extends AnyFlatSpec with Matchers {

  def vm = new VirtualMachine()

  // =========================================================
  // 1. SIMPLE LOOP: decrement to zero
  // =========================================================
  "COW loop" should "decrement a cell to zero using a simple loop" in {
    val out = vm.execute(List(
      MoOSix, MoOSix, MoOSix,   // mem[0] = 3
      MOOSeven,                 // loop start
      MOoFive,                  // mem[0] -= 1
      mooZero                   // loop end
    ))

    vm.getMemory(0) shouldBe 0
  }

  // =========================================================
  // 2. ZERO-TRIP LOOP (should skip entirely)
  // =========================================================
  it should "skip loop body when entry condition is false (zero-trip loop)" in {
    val vm = new VirtualMachine()

    vm.execute(List(
      MOOSeven,   // mem[0] == 0 → skip
      MoOSix,     // should NOT execute
      mooZero
    ))

    vm.getMemory(0) shouldBe 0
  }

  // =========================================================
  // 3. SINGLE ITERATION LOOP
  // =========================================================
  it should "execute loop exactly once when starting value is 1" in {
    val vm = new VirtualMachine()

    vm.execute(List(
      MoOSix,     // mem[0] = 1
      MOOSeven,
      MOoFive,    // mem[0] = 0 → exit next check
      mooZero
    ))

    vm.getMemory(0) shouldBe 0
  }

  // =========================================================
  // 4. LOOP INVARIANT CHECK (counter + accumulator)
  // =========================================================
  it should "maintain correct loop invariants across iterations" in {
    val vm = new VirtualMachine()

    vm.execute(List(
      MoOSix, MoOSix, MoOSix,   // mem[0] = 3 (counter)

      MOOSeven,                 // loop start

      MOoFive,                  // mem[0] -= 1
      moOTwo,                   // pointer → 1
      MoOSix,                   // mem[1] += 1
      mOoOne,                   // pointer → 0

      mooZero
    ))

    vm.getMemory(0) shouldBe 0
    vm.getMemory(1) shouldBe 3
  }

  // =========================================================
  // 5. NESTED LOOP BASIC
  // =========================================================
  it should "handle nested loops correctly (2x2 case)" in {
    val vm = new VirtualMachine()

    vm.execute(List(
      MoOSix, MoOSix,            // mem[0] = 2 (outer)

      MOOSeven,                  // outer loop

      moOTwo,                   // ptr → 1
      MoOSix, MoOSix,           // mem[1] = 2 (inner counter)

      MOOSeven,                 // inner loop
      MOoFive,                  // mem[1] -= 1
      moOTwo,                  // ptr → 2
      MoOSix,                  // mem[2] += 1
      mOoOne,                  // ptr → 1
      mooZero,                 // end inner

      mOoOne,                   // ptr → 0
      MOoFive,                  // mem[0] -= 1

      mooZero                   // end outer
    ))

    vm.getMemory(0) shouldBe 0
    vm.getMemory(1) shouldBe 0
    vm.getMemory(2) shouldBe 4
  }

  // =========================================================
  // 6. NESTED LOOP WITH INDEPENDENT CELLS
  // =========================================================
  it should "preserve correct execution order in nested loops" in {
    val vm = new VirtualMachine()

    vm.execute(List(
      MoOSix, MoOSix,            // mem[0] = 2

      MOOSeven,                 // outer

      moOTwo,
      MoOSix, MoOSix, MoOSix,   // mem[1] = 3

      MOOSeven,                // inner
      MOoFive,                // mem[1] -= 1
      moOTwo,
      MoOSix,                  // mem[2] += 1
      mOoOne,
      mooZero,                 // inner end

      mOoOne,
      MOoFive,                 // mem[0] -= 1

      mooZero
    ))

    vm.getMemory(2) shouldBe 6
  }

  // =========================================================
  // 7. LOOP DOES NOT CORRUPT POINTER STATE
  // =========================================================
  it should "not corrupt pointer across loop iterations" in {
    val vm = new VirtualMachine()

    vm.execute(List(
      MoOSix, MoOSix, MoOSix,   // mem[0] = 3

      MOOSeven,
      MOoFive,                  // decrement mem[0] (NOT mem[1])
      mooZero
    ))

    vm.getPointer shouldBe 0
    vm.getMemory(0) shouldBe 0
  }

  // =========================================================
  // 8. LOOP WITH NO BODY EFFECT
  // =========================================================
  it should "execute loop correctly even if body is empty" in {
    val vm = new VirtualMachine()

    vm.execute(List(
      MoOSix, MoOSix, MoOSix, // mem[0] = 3

      MOOSeven,
      MOoFive, // decrement mem[0] so loop can terminate
      mooZero
    ))

    vm.getMemory(0) shouldBe 0
  }

  // =========================================================
  // 9. LARGE LOOP STABILITY (regression test)
  // =========================================================
  it should "handle larger loop counts without instability" in {
    val vm = new VirtualMachine()

    vm.execute(
      List.fill(10)(MoOSix) ++
        List(
          MOOSeven,
          MOoFive,
          mooZero
        )
    )

    vm.getMemory(0) shouldBe 0
  }

  // =========================================================
  // 10. LOOP NESTING DOES NOT STACK OVERFLOW (pathological safety)
  // =========================================================
  it should "handle deeply nested loops without recursion issues" in {
    val vm = new VirtualMachine()

    vm.execute(List(
      MoOSix, MoOSix,          // mem[0] = 2 (outer)

      MOOSeven,                // outer loop

      moOTwo,                  // move to mem[1]
      MoOSix, MoOSix,          // mem[1] = 2 (inner)

      MOOSeven,                // inner loop

      MOoFive,                 // mem[1] -= 1
      mooZero,                 // end inner loop

      mOoOne,                  // back to mem[0]
      MOoFive,                 // mem[0] -= 1

      mooZero                  // end outer loop
    ))

    vm.getMemory(0) shouldBe 0
  }
}