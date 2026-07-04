
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Command._

/**
 * Spec-compliant test suite for the COW language VM.
 *
 * Instruction reference (Sean Heber / esolangs.org):
 *   code 0  - moo  : jump back to matching MOO (unconditional)
 *   code 1  - mOo  : move memory pointer left one block
 *   code 2  - moO  : move memory pointer right one block
 *   code 3  - mOO  : execute current memory value as instruction (code 3 = halt)
 *   code 4  - Moo  : if mem==0 read ASCII char; else print ASCII char
 *   code 5  - MOo  : decrement current memory block
 *   code 6  - MoO  : increment current memory block
 *   code 7  - MOO  : if mem==0 skip to after matching moo; else continue
 *   code 8  - OOO  : set current memory block to 0
 *   code 9  - MMM  : copy mem→register (if empty) or paste register→mem (if full)
 *   code 10 - OOM  : print current memory block as integer
 *   code 11 - oom  : read integer from STDIN into current memory block
 */
class CowCommandSpec extends AnyFlatSpec with Matchers {

  // ─────────────────────────────────────────────
  // Helpers
  // ─────────────────────────────────────────────

  def freshVm(): VirtualMachine = new VirtualMachine()

  // ─────────────────────────────────────────────
  // code 6 — MoO  (increment)
  // ─────────────────────────────────────────────

  "MoO (code 6)" should "increment memory block by 1" in {
    val vm = freshVm()
    vm.execute(List(MoOSix))
    vm.getMemory(0) shouldBe 1
  }

  it should "increment multiple times" in {
    val vm = freshVm()
    vm.execute(List(MoOSix, MoOSix, MoOSix))
    vm.getMemory(0) shouldBe 3
  }

  // ─────────────────────────────────────────────
  // code 5 — MOo  (decrement)
  // ─────────────────────────────────────────────

  "MOo (code 5)" should "decrement memory block by 1" in {
    val vm = freshVm()
    vm.execute(List(MoOSix, MoOSix, MOoFive))
    vm.getMemory(0) shouldBe 1
  }

  it should "allow negative values" in {
    val vm = freshVm()
    vm.execute(List(MOoFive))
    vm.getMemory(0) shouldBe -1
  }

  // ─────────────────────────────────────────────
  // code 8 — OOO  (zero current block)
  // ─────────────────────────────────────────────

  "OOO (code 8)" should "set current memory block to 0" in {
    val vm = freshVm()
    vm.execute(List(MoOSix, MoOSix, MoOSix, OOOEight))
    vm.getMemory(0) shouldBe 0
  }

  it should "be a no-op when block is already 0" in {
    val vm = freshVm()
    vm.execute(List(OOOEight))
    vm.getMemory(0) shouldBe 0
  }

  // ─────────────────────────────────────────────
  // code 2 — moO  (pointer right)
  // code 1 — mOo  (pointer left)
  // ─────────────────────────────────────────────

  "moO (code 2)" should "move the memory pointer forward one block" in {
    val vm = freshVm()
    vm.execute(List(moOTwo))
    vm.getPointer shouldBe 1
  }

  it should "allow writing to the new block independently" in {
    val vm = freshVm()
    vm.execute(List(MoOSix, moOTwo, MoOSix, MoOSix))
    vm.getMemory(0) shouldBe 1
    vm.getMemory(1) shouldBe 2
  }

  "mOo (code 1)" should "move the memory pointer back one block" in {
    val vm = freshVm()
    vm.execute(List(moOTwo, mOoOne))
    vm.getPointer shouldBe 0
  }

  it should "not move below 0 (boundary guard)" in {
    val vm = freshVm()
    vm.execute(List(mOoOne)) // already at 0
    vm.getPointer shouldBe 0
  }

  it should "round-trip: move right then left returns to origin" in {
    val vm = freshVm()
    vm.execute(List(moOTwo, moOTwo, moOTwo, mOoOne, mOoOne, mOoOne))
    vm.getPointer shouldBe 0
  }

  // ─────────────────────────────────────────────
  // code 9 — MMM  (register copy/paste)
  // ─────────────────────────────────────────────

  it should "paste register value into current memory block and clear register" in {
    val vm = freshVm()
    // Set mem[0]=5, copy to register, move to mem[1], paste
    vm.execute(List(
      MoOSix, MoOSix, MoOSix, MoOSix, MoOSix, // mem[0] = 5
      MMMNine,                                   // register = Some(5)
      moOTwo,                                    // pointer → 1
      MMMNine                                    // mem[1] = 5, register = None
    ))
    vm.getMemory(1) shouldBe 5
  }

  it should "copy value to a second cell without modifying the first" in {
    val vm = freshVm()
    vm.execute(List(
      MoOSix, MoOSix,  // mem[0] = 2
      MMMNine,          // register = Some(2)
      moOTwo,           // pointer → 1
      MMMNine           // mem[1] = 2
    ))
    vm.getMemory(0) shouldBe 2
    vm.getMemory(1) shouldBe 2
  }

  // ─────────────────────────────────────────────
  // code 10 — OOM  (print as integer)
  // ─────────────────────────────────────────────

  "OOM (code 10)" should "output the current memory block value as an integer" in {
    val vm = freshVm()
    val out = vm.execute(List(MoOSix, MoOSix, MoOSix, OOMTen))
    out shouldBe "3"
  }

  it should "output 0 when memory block is 0" in {
    val vm = freshVm()
    val out = vm.execute(List(OOMTen))
    out shouldBe "0"
  }

  it should "output multiple integers in sequence" in {
    val vm = freshVm()
    val out = vm.execute(List(
      MoOSix, MoOSix, OOMTen,  // print 2
      moOTwo,                    // pointer → 1
      MoOSix, OOMTen             // print 1
    ))
    out shouldBe "21"
  }

  // ─────────────────────────────────────────────
  // code 4 — Moo  (ASCII I/O)
  // ─────────────────────────────────────────────

  "Moo (code 4)" should "print the ASCII character for the current memory value when non-zero" in {
    val vm = freshVm()
    // 65 = 'A'
    val out = vm.execute(
      List.fill(65)(MoOSix) :+ MooFour
    )
    out shouldBe "A"
  }

  it should "print multiple ASCII characters" in {
    val vm = freshVm()
    // 72 = 'H', 105 = 'i'
    val out = vm.execute(
      List.fill(72)(MoOSix) ++
        List(MooFour, OOOEight) ++
        List.fill(105)(MoOSix) ++
        List(MooFour)
    )
    out shouldBe "Hi"
  }

  // ─────────────────────────────────────────────
  // code 7 + code 0 — MOO / moo  (loops)
  // ─────────────────────────────────────────────

  "MOO/moo (codes 7/0)" should "not enter loop body when memory is 0" in {
    val vm = freshVm()
    // mem[0] == 0 → MOO skips to after moo; MoO inside never runs
    vm.execute(List(
      MOOSeven,  // 0: mem==0 → skip to after index 2
      MoOSix,    // 1: (skipped)
      mooZero    // 2: matching close
    ))
    vm.getMemory(0) shouldBe 0
  }

  it should "execute loop body when memory is non-zero and decrement to zero" in {
    val vm = freshVm()
    vm.execute(List(
      MoOSix, MoOSix, MoOSix, // mem[0] = 3
      MOOSeven,                // loop open
      MOoFive,                 // mem[0] -= 1
      mooZero                  // loop close → back to MOO
    ))
    vm.getMemory(0) shouldBe 0
  }

  it should "run loop body exactly N times" in {
    val vm = freshVm()
    // mem[0] counts down from 5; mem[1] counts up — should equal 5
    vm.execute(List(
      MoOSix, MoOSix, MoOSix, MoOSix, MoOSix, // mem[0] = 5
      MOOSeven,                                  // loop open
      MOoFive,                                 // mem[0] -= 1
      moOTwo,                                  // pointer → 1
      MoOSix,                                  // mem[1] += 1
      mOoOne,                                  // pointer → 0
      mooZero                                    // loop close
    ))
    vm.getMemory(0) shouldBe 0
    vm.getMemory(1) shouldBe 5
  }

  it should "handle nested loops correctly" in {
    val vm = freshVm()

    vm.execute(List(
      MoOSix, MoOSix, MoOSix,     // mem[0] = 3 (outer)

      MOOSeven,                   // outer loop start

      moOTwo,                     // pointer → 1
      MoOSix, MoOSix,            // mem[1] = 2 (inner counter)

      MOOSeven,                  // inner loop start
      MOoFive,                  // mem[1] -= 1
      MoOSix,                  // mem[2] += 1 (accumulator)
      mooZero,                   // inner loop end

      mOoOne,                    // pointer → 0
      MOoFive,                   // mem[0] -= 1

      mooZero                    // outer loop end
    ))

    vm.getMemory(0) shouldBe 0
    vm.getMemory(1) shouldBe 0
    vm.getMemory(2) shouldBe 6
  }

  it should "handle nested loops with separate counter cells" in {
    val vm = freshVm()
    // mem[0] = outer counter (2)
    // mem[1] = inner counter (3 per outer iteration)
    // mem[2] = accumulator (incremented once per inner iteration → 2*3 = 6)
    vm.execute(List(
      MoOSix, MoOSix,            // mem[0] = 2

      MOOSeven,                   // [outer — runs while mem[0] != 0

      // set inner counter
      moOTwo,                   // pointer → 1
      MoOSix, MoOSix, MoOSix,  // mem[1] = 3

      MOOSeven,                 // [inner — runs while mem[1] != 0
      MOoFive,                // mem[1] -= 1
      moOTwo,                 // pointer → 2
      MoOSix,                 // mem[2] += 1
      mOoOne,                 // pointer → 1
      mooZero,                  // ]inner

      mOoOne,                   // pointer → 0
      MOoFive,                  // mem[0] -= 1

      mooZero                     // ]outer
    ))
    vm.getMemory(0) shouldBe 0
    vm.getMemory(1) shouldBe 0
    vm.getMemory(2) shouldBe 6
  }

  it should "skip loop entirely when entry condition is false (zero-trip)" in {
    val vm = freshVm()
    vm.execute(List(
      // mem[0] is 0 — loop body must never execute
      MOOSeven,
      MoOSix,  // would set mem[0]=1 if executed
      mooZero
    ))
    vm.getMemory(0) shouldBe 0
  }

  it should "handle a loop that runs exactly once" in {
    val vm = freshVm()
    vm.execute(List(
      MoOSix,   // mem[0] = 1
      MOOSeven,
      MOoFive, // mem[0] = 0
      mooZero
    ))
    vm.getMemory(0) shouldBe 0
  }

  // ─────────────────────────────────────────────
  // code 3 — mOO  (execute memory value as instruction)
  // ─────────────────────────────────────────────

  "mOO (code 3)" should "execute the value in memory as an instruction" in {
    val vm = freshVm()
    // mem[0] = 6 (MoO), then mOO executes it → mem[0] becomes 7
    vm.execute(List(
      MoOSix, MoOSix, MoOSix,
      MoOSix, MoOSix, MoOSix, // mem[0] = 6
      mOOThree                 // execute code 6 = MoO → mem[0] = 7
    ))
    vm.getMemory(0) shouldBe 7
  }

  it should "halt when memory value is 3 (would cause infinite recursion)" in {
    val vm = freshVm()
    // mem[0] = 3, mOO tries to execute code 3 → must halt
    vm.execute(List(
      MoOSix, MoOSix, MoOSix, // mem[0] = 3
      mOOThree                 // code 3 is invalid → halt
    ))
    // VM should have halted; memory[0] remains 3
    vm.getMemory(0) shouldBe 3
  }

  it should "halt on an invalid instruction code" in {
    val vm = freshVm()
    // mem[0] = 99 (no such instruction) → halt
    // Build 99 via loop to avoid giant List
    vm.execute(List(
      // set mem[0] to 99 using a loop on mem[1] as counter
      moOTwo,                                    // pointer → 1
      MoOSix, MoOSix, MoOSix, MoOSix, MoOSix,  // mem[1] = 5 (loop 5 times, +20 each? no — simpler below)
      mOoOne,                                    // pointer → 0
      // Just directly increment 99 times (verbose but unambiguous)
      // Use a helper: set mem[0] = 12 (last valid code) + 1 = 13 → invalid
      MoOSix, MoOSix, MoOSix, MoOSix, MoOSix,
      MoOSix, MoOSix, MoOSix, MoOSix, MoOSix,
      MoOSix, MoOSix, MoOSix,                   // mem[0] = 13 (invalid)
      mOOThree                                   // execute code 13 → halt
    ))
    vm.getMemory(0) shouldBe 13
  }

  // ─────────────────────────────────────────────
  // CowCommand.fromCode — companion object
  // ─────────────────────────────────────────────

  "CowCommand.fromCode" should "return the correct command for each valid code" in {
    CowCommand.fromCode(0)  shouldBe Some(mooZero)
    CowCommand.fromCode(1)  shouldBe Some(mOoOne)
    CowCommand.fromCode(2)  shouldBe Some(moOTwo)
    CowCommand.fromCode(3)  shouldBe Some(mOOThree)
    CowCommand.fromCode(4)  shouldBe Some(MooFour)
    CowCommand.fromCode(5)  shouldBe Some(MOoFive)
    CowCommand.fromCode(6)  shouldBe Some(MoOSix)
    CowCommand.fromCode(7)  shouldBe Some(MOOSeven)
    CowCommand.fromCode(8)  shouldBe Some(OOOEight)
    CowCommand.fromCode(9)  shouldBe Some(MMMNine)
    CowCommand.fromCode(10) shouldBe Some(OOMTen)
    CowCommand.fromCode(11) shouldBe Some(oomEleven)
  }

  it should "return None for any code outside 0–11" in {
    CowCommand.fromCode(-1) shouldBe None
    CowCommand.fromCode(12) shouldBe None
    CowCommand.fromCode(99) shouldBe None
  }

  // ─────────────────────────────────────────────
  // Each command's .code field
  // ─────────────────────────────────────────────

  "Each CowCommand" should "report the correct code value" in {
    mooZero.code   shouldBe 0
    mOoOne.code    shouldBe 1
    moOTwo.code    shouldBe 2
    mOOThree.code  shouldBe 3
    MooFour.code   shouldBe 4
    MOoFive.code   shouldBe 5
    MoOSix.code    shouldBe 6
    MOOSeven.code  shouldBe 7
    OOOEight.code  shouldBe 8
    MMMNine.code   shouldBe 9
    OOMTen.code    shouldBe 10
    oomEleven.code shouldBe 11
  }

  // ─────────────────────────────────────────────
  // Integration — multi-instruction programs
  // ─────────────────────────────────────────────

  "Integration" should "copy a value to an adjacent cell using MMM" in {
    val vm = freshVm()
    vm.execute(List(
      MoOSix, MoOSix, MoOSix, MoOSix, // mem[0] = 4
      MMMNine,                          // register ← 4
      moOTwo,                           // pointer → 1
      MMMNine                           // mem[1] ← 4, register cleared
    ))
    vm.getMemory(0) shouldBe 4
    vm.getMemory(1) shouldBe 4
  }

  it should "zero a cell then increment it independently" in {
    val vm = freshVm()
    vm.execute(List(
      MoOSix, MoOSix, MoOSix, // mem[0] = 3
      OOOEight,                // mem[0] = 0
      MoOSix                   // mem[0] = 1
    ))
    vm.getMemory(0) shouldBe 1
  }

  it should "output integer then ASCII in sequence" in {
    val vm = freshVm()
    // Print integer 2, then ASCII for 65 ('A')
    val out = vm.execute(
      List(MoOSix, MoOSix, OOMTen, OOOEight) ++
        List.fill(65)(MoOSix) ++
        List(MooFour)
    )
    out shouldBe "2A"
  }

  it should "use a loop to build a value rather than repeating MoO" in {
    val vm = freshVm()
    // mem[1] = 4 (loop counter), mem[0] accumulates +3 per iteration → 12
    vm.execute(List(
      moOTwo,                            // pointer → 1
      MoOSix, MoOSix, MoOSix, MoOSix,  // mem[1] = 4
      MOOSeven,                          // [loop on mem[1]
      mOoOne,                          // pointer → 0
      MoOSix, MoOSix, MoOSix,         // mem[0] += 3
      moOTwo,                          // pointer → 1
      MOoFive,                         // mem[1] -= 1
      mooZero                            // ]loop
    ))
    vm.getMemory(0) shouldBe 12
    vm.getMemory(1) shouldBe 0
  }
}
