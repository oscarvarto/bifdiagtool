package umich.parser

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers

class MapTest extends FunSpec with ShouldMatchers {
  describe("A NameMap") {
    import Names._
    import shapeless._

    val normalMap = Map(ConstName("alpha") → 2.0, ConstName("omega") → 3.0)
    val hmap = HMap[NameMap](VarName("x") → 1.0, ParamName("r") → -3.0)

    it("It can add every pair of a normal map to it") {
      val hmap2 = mapAdd(normalMap, hmap)
      hmap2.get(VarName("x")) should be(Some(1.0))
      hmap2.get(ParamName("r")) should be(Some(-3.0))
      hmap2.get(ConstName("alpha")) should be(Some(2.0))
      hmap2.get(ConstName("omega")) should be(Some(3.0))
    }
  }
}
