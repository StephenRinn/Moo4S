import scala.language.postfixOps

object Command {
  sealed trait CowCommand {
    def code: Int
  }

  case object mooZero extends CowCommand {
    val code = 0
  }

  case object mOoOne extends CowCommand {
    val code = 1
  }

  case object moOTwo extends CowCommand {
    val code = 2
  }

  case object mOOThree extends CowCommand {
    val code = 3
  }

  case object MooFour extends CowCommand {
    val code = 4
  }

  case object MOoFive extends CowCommand {
    val code = 5
  }

  case object MoOSix extends CowCommand {
    val code = 6
  }

  case object MOOSeven extends CowCommand {
    val code = 7
  }

  case object OOOEight extends CowCommand {
    val code = 8
  }

  case object MMMNine extends CowCommand {
    val code = 9
  }

  case object OOMTen extends CowCommand {
    val code = 10
  }

  case object oomEleven extends CowCommand {
    val code = 11
  }

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