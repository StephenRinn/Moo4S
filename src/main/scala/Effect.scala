sealed trait Effect
case object Continue extends Effect
case class Jump(to: Int) extends Effect
case object Halt extends Effect