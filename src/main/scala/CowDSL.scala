/**
 * COW DSL (Domain Specific Language) for writing COW programs in Scala
 *
 * Usage:
 *   import CowDSL._
 *
 *   def myProgram(): String = {
 *     Moo
 *     moo
 *     MOo
 *     MoO
 *   }
 */
object CowDSL {

  // Thread-local storage for accumulating commands during DSL execution
  private val commandBuffer = new ThreadLocal[scala.collection.mutable.ListBuffer[Command.CowCommand]] {
    override def initialValue() = scala.collection.mutable.ListBuffer[Command.CowCommand]()
  }

  /**
   * Execute a block of COW DSL code and return the program output
   *
   * Usage:
   *   val output = cow {
   *     Moo
   *     moo
   *     MOo
   *   }
   */
  def cow(block: => Unit): String = {
    commandBuffer.get().clear()
    block
    val commands = commandBuffer.get().toList
    val vm = new VirtualMachine()
    vm.execute(commands)
  }

  /**
   * Execute a block of COW DSL code with custom memory size
   */
  def cow(memorySize: Int)(block: => Unit): String = {
    commandBuffer.get().clear()
    block
    val commands = commandBuffer.get().toList
    val vm = new VirtualMachine(memorySize)
    vm.execute(commands)
  }

  /**
   * Get the accumulated commands without executing
   * Useful for testing or analysis
   */
  def cowCommands(block: => Unit): List[Command.CowCommand] = {
    commandBuffer.get().clear()
    block
    commandBuffer.get().toList
  }

  // ===== COW Instructions =====

  /**
   * moo (code 0) - Loop back to matching MOO
   */
  def moo: Unit = commandBuffer.get() += Command.mooZero

  /**
   * mOo (code 1) - Move pointer left
   */
  def mOo: Unit = commandBuffer.get() += Command.mOoOne

  /**
   * moO (code 2) - Move pointer right
   */
  def moO: Unit = commandBuffer.get() += Command.moOTwo

  /**
   * mOO (code 3) - Execute value as instruction
   */
  def mOO: Unit = commandBuffer.get() += Command.mOOThree

  /**
   * Moo (code 4) - I/O operation
   */
  def Moo: Unit = commandBuffer.get() += Command.MooFour

  /**
   * MOo (code 5) - Decrement
   */
  def MOo: Unit = commandBuffer.get() += Command.MOoFive

  /**
   * MoO (code 6) - Increment
   */
  def MoO: Unit = commandBuffer.get() += Command.MoOSix

  /**
   * MOO (code 7) - Conditional loop (if current == 0, skip to matching moo)
   */
  def MOO: Unit = commandBuffer.get() += Command.MOOSeven

  /**
   * OOO (code 8) - Set to zero
   */
  def OOO: Unit = commandBuffer.get() += Command.OOOEight

  /**
   * MMM (code 9) - Copy/paste register
   */
  def MMM: Unit = commandBuffer.get() += Command.MMMNine

  /**
   * OOM (code 10) - Print as integer
   */
  def OOM: Unit = commandBuffer.get() += Command.OOMTen

  /**
   * oom (code 11) - Read integer
   */
  def oom: Unit = commandBuffer.get() += Command.oomEleven
}

/**
 * Trait for extending classes with COW DSL support
 *
 * Usage:
 *   class MyProgram extends CowProgram {
 *     def foo(): String = {
 *       Moo
 *       moo
 *       MOo
 *       MoO
 *     }
 *   }
 */
trait CowProgram {

  // Thread-local storage for accumulating commands
  private val commandBuffer = new ThreadLocal[scala.collection.mutable.ListBuffer[Command.CowCommand]] {
    override def initialValue() = scala.collection.mutable.ListBuffer[Command.CowCommand]()
  }

  /**
   * Execute a block and return the output
   * Used internally by DSL methods
   */
  protected def executeBlock(block: => Unit): String = {
    commandBuffer.get().clear()
    block
    val commands = commandBuffer.get().toList
    val vm = new VirtualMachine()
    vm.execute(commands)
  }

  /**
   * Execute a block with custom memory size
   */
  protected def executeBlock(memorySize: Int)(block: => Unit): String = {
    commandBuffer.get().clear()
    block
    val commands = commandBuffer.get().toList
    val vm = new VirtualMachine(memorySize)
    vm.execute(commands)
  }

  /**
   * Get accumulated commands without executing
   */
  protected def getCommands(block: => Unit): List[Command.CowCommand] = {
    commandBuffer.get().clear()
    block
    commandBuffer.get().toList
  }

  // ===== COW Instructions =====

  protected def moo: Unit = commandBuffer.get() += Command.mooZero
  protected def mOo: Unit = commandBuffer.get() += Command.mOoOne
  protected def moO: Unit = commandBuffer.get() += Command.moOTwo
  protected def mOO: Unit = commandBuffer.get() += Command.mOOThree
  protected def Moo: Unit = commandBuffer.get() += Command.MooFour
  protected def MOo: Unit = commandBuffer.get() += Command.MOoFive
  protected def MoO: Unit = commandBuffer.get() += Command.MoOSix
  protected def MOO: Unit = commandBuffer.get() += Command.MOOSeven
  protected def OOO: Unit = commandBuffer.get() += Command.OOOEight
  protected def MMM: Unit = commandBuffer.get() += Command.MMMNine
  protected def OOM: Unit = commandBuffer.get() += Command.OOMTen
  protected def oom: Unit = commandBuffer.get() += Command.oomEleven
}
