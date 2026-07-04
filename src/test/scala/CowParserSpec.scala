import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers


class CowParserSpec extends AnyFlatSpec with Matchers {

  "CowParser" should "parse a single token to a command" in {
    val tokens = List(MooTokenZero)
    val commands = CowParser.parse(tokens)
    commands should equal(List(Command.mooZero))
  }

  it should "parse all 12 token types to their corresponding commands" in {
    val tokens = List(
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
    )
    val commands = CowParser.parse(tokens)
    commands should equal(List(
      Command.mooZero,
      Command.mOoOne,
      Command.moOTwo,
      Command.mOOThree,
      Command.MooFour,
      Command.MOoFive,
      Command.MoOSix,
      Command.MOOSeven,
      Command.OOOEight,
      Command.MMMNine,
      Command.OOMTen,
      Command.oomEleven
    ))
  }

  it should "parse multiple tokens in sequence" in {
    val tokens = List(
      MooInstrTokenFour,
      MOoTokenOne,
      MoOTokenTwo,
      OOMTokenTen
    )
    val commands = CowParser.parse(tokens)
    commands should equal(List(
      Command.MooFour,
      Command.mOoOne,
      Command.moOTwo,
      Command.OOMTen
    ))
  }

  it should "handle empty token list" in {
    val tokens = List()
    val commands = CowParser.parse(tokens)
    commands should equal(List())
  }

  it should "filter out unknown tokens" in {
    val tokens = List(
      MooTokenZero,
      UnknownToken("invalid"),
      MOoTokenOne,
      UnknownToken("also invalid"),
      MoOTokenTwo
    )
    val commands = CowParser.parse(tokens)
    commands should equal(List(
      Command.mooZero,
      Command.mOoOne,
      Command.moOTwo
    ))
  }

  it should "handle all unknown tokens" in {
    val tokens = List(
      UnknownToken("invalid1"),
      UnknownToken("invalid2"),
      UnknownToken("invalid3")
    )
    val commands = CowParser.parse(tokens)
    commands should equal(List())
  }

  it should "preserve command order" in {
    val tokens = List(
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
    )
    val commands = CowParser.parse(tokens)
    commands should equal(List(
      Command.OOMTen,
      Command.MMMNine,
      Command.OOOEight,
      Command.MOOSeven,
      Command.MoOSix,
      Command.MOoFive,
      Command.MooFour,
      Command.mOOThree,
      Command.moOTwo,
      Command.mOoOne,
      Command.mooZero
    ))
  }

  it should "parse a simple loop structure" in {
    val tokens = List(
      MOOInstrTokenSeven,
      MooInstrTokenFour,
      OOMTokenTen,
      MooTokenZero
    )
    val commands = CowParser.parse(tokens)
    commands should equal(List(
      Command.MOOSeven,
      Command.MooFour,
      Command.OOMTen,
      Command.mooZero
    ))
  }

  it should "parse nested loops" in {
    val tokens = List(
      MOOInstrTokenSeven,
      MOOInstrTokenSeven,
      MooInstrTokenFour,
      MooTokenZero,
      MooTokenZero
    )
    val commands = CowParser.parse(tokens)
    commands should equal(List(
      Command.MOOSeven,
      Command.MOOSeven,
      Command.MooFour,
      Command.mooZero,
      Command.mooZero
    ))
  }

  it should "parse a complex program" in {
    val tokens = List(
      MooInstrTokenFour,
      MOoTokenOne,
      MoOTokenTwo,
      MOOInstrTokenSeven,
      MooInstrTokenFour,
      MooTokenZero,
      OOMTokenTen,
      MMMTokenNine
    )
    val commands = CowParser.parse(tokens)
    commands should equal(List(
      Command.MooFour,
      Command.mOoOne,
      Command.moOTwo,
      Command.MOOSeven,
      Command.MooFour,
      Command.mooZero,
      Command.OOMTen,
      Command.MMMNine
    ))
  }

  it should "parseFromSource with simple code" in {
    val source = "moo MOo moO"
    val commands = CowParser.parseFromSource(source)
    commands should equal(List(
      Command.mooZero,
      Command.MOoFive,
      Command.moOTwo
    ))
  }

  it should "parseFromSource with whitespace and non-instruction text" in {
    val source = "moo hello MOo world moO"
    val commands = CowParser.parseFromSource(source)
    commands should equal(List(
      Command.mooZero,
      Command.MOoFive,
      Command.moOTwo
    ))
  }

  it should "parseFromSource with newlines and tabs" in {
    val source = """moo
                   |MOo
                   |moO
                   |mOO""".stripMargin
    val commands = CowParser.parseFromSource(source)
    commands should equal(List(
      Command.mooZero,
      Command.MOoFive,
      Command.moOTwo,
      Command.mOOThree
    ))
  }

  it should "parseFromSource with empty string" in {
    val source = ""
    val commands = CowParser.parseFromSource(source)
    commands should equal(List())
  }

  it should "parseFromSource with only whitespace" in {
    val source = "   \n\n\t\t  "
    val commands = CowParser.parseFromSource(source)
    commands should equal(List())
  }

  it should "parseFromSource with only non-instruction text" in {
    val source = "hello world this is not cow code"
    val commands = CowParser.parseFromSource(source)
    commands should equal(List())
  }

  it should "parseFromSource with consecutive instructions" in {
    val source = "mooMOomoOMOO"
    val commands = CowParser.parseFromSource(source)
    commands should equal(List(
      Command.mooZero,
      Command.MOoFive,
      Command.moOTwo,
      Command.MOOSeven
    ))
  }

  it should "parseFromSource with mixed spacing" in {
    val source = "moo MOo  moO   mOO Moo MOo"
    val commands = CowParser.parseFromSource(source)
    commands should equal(List(
      Command.mooZero,
      Command.MOoFive,
      Command.moOTwo,
      Command.mOOThree,
      Command.MooFour,
      Command.MOoFive
    ))
  }

  it should "parseFromSource with a fibonacci example" in {
    val source = "MoO moO MoO mOo MOO OOM MMM moO moO MMM mOo mOo moO MMM mOo MMM moO moO MOO MOo mOo MoO moO moo mOo mOo moo"
    val commands = CowParser.parseFromSource(source)
    commands.length should equal(27)
    commands.head should equal(Command.MoOSix)
    commands.last should equal(Command.mooZero)
  }

  it should "handle very long token lists" in {
    val tokens = List.fill(1000)(MooTokenZero)
    val commands = CowParser.parse(tokens)
    commands.length should equal(1000)
    commands.forall(_ == Command.mooZero) should be(true)
  }

  it should "handle alternating token types" in {
    val tokens = List(
      MooTokenZero,
      MOoTokenOne,
      MooTokenZero,
      MOoTokenOne,
      MooTokenZero
    )
    val commands = CowParser.parse(tokens)
    commands should equal(List(
      Command.mooZero,
      Command.mOoOne,
      Command.mooZero,
      Command.mOoOne,
      Command.mooZero
    ))
  }

  it should "map each token type exactly once" in {
    val tokenCommandPairs = List(
      (MooTokenZero, Command.mooZero),
      (MOoTokenOne, Command.mOoOne),
      (MoOTokenTwo, Command.moOTwo),
      (MOOTokenThree, Command.mOOThree),
      (MooInstrTokenFour, Command.MooFour),
      (MOoInstrTokenFive, Command.MOoFive),
      (MoOInstrTokenSix, Command.MoOSix),
      (MOOInstrTokenSeven, Command.MOOSeven),
      (OOOTokenEight, Command.OOOEight),
      (MMMTokenNine, Command.MMMNine),
      (OOMTokenTen, Command.OOMTen),
      (OomTokenEleven, Command.oomEleven)
    )

    tokenCommandPairs.foreach { case (token, expectedCommand) =>
      val commands = CowParser.parse(List(token))
      commands should equal(List(expectedCommand))
    }
  }

}
