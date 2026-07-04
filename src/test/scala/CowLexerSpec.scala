import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CowLexerSpec extends AnyFlatSpec with Matchers {
  val correctFullList: List[CowToken] = List(
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
    OomTokenEleven,
  )

  "CowLexer" should "tokenize a single moo instruction" in {
    val moo = "moo"
    val tokens = CowLexer.tokenize(moo)
    tokens should equal(correctFullList.take(1))
  }

  it should "tokenize all 12 instructions" in {
    val moos = "moo mOo moO mOO Moo MOo MoO MOO OOO MMM OOM oom"
    val tokens = CowLexer.tokenize(moos)
    tokens should equal(
      correctFullList
    )
  }

  it should "ignore whitespace between instructions" in {
    val moos = "moo    mOo\n\nmoO\t\tmOO"
    val tokens = CowLexer.tokenize(moos)
    tokens should equal(correctFullList.take(4))
  }

  it should "ignore non-instruction characters" in {
    val code = "moo hello mOo world moO"
    val tokens = CowLexer.tokenize(code)
    tokens should equal(correctFullList.take(3))
  }

  it should "handle empty input" in {
    val code = ""
    val tokens = CowLexer.tokenize(code)
    tokens should equal(List())
  }

  it should "handle input with only non-instruction characters" in {
    val code = "hello world this is not cow"
    val tokens = CowLexer.tokenize(code)
    tokens should equal(List())
  }

  it should "tokenize consecutive instructions without spaces" in {
    val code = "mooMOomoOMOO"
    val tokens = CowLexer.tokenize(code)
    tokens should equal(List(MooTokenZero, MOoInstrTokenFive, MoOTokenTwo, MOOInstrTokenSeven))
  }
}
