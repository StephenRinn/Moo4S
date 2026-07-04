import scala.language.postfixOps

object Command {
  sealed trait CowCommand {
    def code: Int
    def execute(vm: VirtualMachine): String
  }

  case object mooZero extends CowCommand {
    val code = 0
    def execute(vm: VirtualMachine): String = vm.execute(List(mooZero))
  }

  val moo: CowCommand = mooZero

  case object mOoOne extends CowCommand {
    val code = 1
    def execute(vm: VirtualMachine): String = vm.execute(List(mOoOne))
  }
  
  val mOo: CowCommand = mOoOne

  case object moOTwo extends CowCommand {
    val code = 2
    def execute(vm: VirtualMachine): String = vm.execute(List(moOTwo))
  }

  val moO: CowCommand = moOTwo
  
  case object mOOThree extends CowCommand {
    val code = 3
    def execute(vm: VirtualMachine): String = vm.execute(List(mOOThree))
  }

  val mOO: CowCommand = mOOThree
  
  case object MooFour extends CowCommand {
    val code = 4
    def execute(vm: VirtualMachine): String = vm.execute(List(MooFour))
  }
  
  val Moo: CowCommand = MooFour

  case object MOoFive extends CowCommand {
    val code = 5
    def execute(vm: VirtualMachine): String = vm.execute(List(MOoFive))
  }

  val MOo: CowCommand = MOoFive
  
  case object MoOSix extends CowCommand {
    val code = 6
    def execute(vm: VirtualMachine): String = vm.execute(List(MoOSix))
  }
  
  val MoO: CowCommand = MoOSix

  case object MOOSeven extends CowCommand {
    val code = 7
    def execute(vm: VirtualMachine): String = vm.execute(List(MOOSeven))
  }

  val MOO: CowCommand = MOOSeven
  
  case object OOOEight extends CowCommand {
    val code = 8
    def execute(vm: VirtualMachine): String = vm.execute(List(OOOEight))
  }

  val OOO: CowCommand = OOOEight
  
  case object MMMNine extends CowCommand {
    val code = 9
    def execute(vm: VirtualMachine): String = vm.execute(List(MMMNine))
  }

  val MMM: CowCommand = MMMNine
  
  case object OOMTen extends CowCommand {
    val code = 10
    def execute(vm: VirtualMachine): String = vm.execute(List(OOMTen))
  }
  
  val OOM: CowCommand = OOMTen

  case object oomEleven extends CowCommand {
    val code = 11
    def execute(vm: VirtualMachine): String = vm.execute(List(oomEleven))
  }
  
  val oom: CowCommand = oomEleven

  object CowCommand {
    def fromCode(code: Int): Option[CowCommand] = code match {
      case 0 => Some(mooZero)
      case 1 => Some(mOoOne)
      case 2 => Some(moOTwo)
      case 3 => Some(mOOThree)
      case 4 => Some(MooFour)
      case 5 => Some(MOoFive)
      case 6 => Some(MoOSix)
      case 7 => Some(MOOSeven)
      case 8 => Some(OOOEight)
      case 9 => Some(MMMNine)
      case 10 => Some(OOMTen)
      case 11 => Some(oomEleven)
      case _ => None
    }
  }
}