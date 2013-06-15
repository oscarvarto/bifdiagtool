package umich.simulation

import org.scalatest.{ FunSuite, Matchers, GivenWhenThen }

class MapsTest extends FunSuite with Matchers with GivenWhenThen {
  test("simple usage of appendMap") {
    Given("An initial HMap[NameMap] and a Map[T, Double], where T <: Name")
    import shapeless._
    import umich.parser.Names._

    val initHMap: HMap[NameMap] = HMap[NameMap](
      ParamName("_a") → 2.5,
      VarName("x") → 5.0)
    val paramsToDoubles = Map(ParamName("_b") → 4.0)

    import umich.bidiatool.polyFuncs.appendMap

    val finalHMap = (initHMap :: paramsToDoubles :: HNil).reduceLeft(appendMap)
    finalHMap.get(ParamName("_a")) should be(Some(2.5))
    finalHMap.get(VarName("x")) should be(Some(5.0))
    finalHMap.get(ParamName("_b")) should be(Some(4.0))
  }
}
