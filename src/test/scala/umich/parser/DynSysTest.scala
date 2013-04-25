package umich.parser

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

import org.scalamock.scalatest.MockFactory

import Names._

class DynSysTest extends FunSuite with MockFactory {

  lazy val dynSys = mock[DynSys]
  test("A Dynamical System can be evaluated with an array of double as input") {

    import shapeless.HMap

    val input = HMap[NameMap](
      VarName("x1") → 2.0,
      VarName("x2") → 0.0,
      ParamName("_w") → 3.0)

    (dynSys.eval _).expects(input)
    dynSys.eval(input)
  }
}
