package umich.simulation

import org.scalatest.FunSuite
import org.scalatest.Matchers

class ParameterConfigTest extends FunSuite with Matchers {
  import scalaz.NonEmptyList
  import scalaz.syntax.validation._
  import ParameterConfig._

  test("From must be less or equal than To, otherwise explain this") {
    ParameterConfig(2.0, 3.0, 0.1).isSuccess should be(true)
    ParameterConfig(3.0, 2.0, 0.1) should be(cond1Msg.failNel)
  }

  test("Step must be less than or equal than given Range, otherwise explain this") {
    ParameterConfig(0.0, 1.0, 0.1).isSuccess should be(true)
    ParameterConfig(0.0, 1.0, 2.0) should be(cond2Msg.failNel)
  }

  test("If neither condition is fulfilled, errors should accumulate") {
    ParameterConfig(3.0, 2.0, 2.0) should be(
      NonEmptyList(cond1Msg, cond2Msg).fail)
  }
}
