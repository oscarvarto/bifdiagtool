package umich.parser

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import scala.collection.immutable.TreeSet

class DynSys1Test extends FunSpec with ShouldMatchers {
  import DynSys1.dynSys
  import DynSys1.input
  import Names._

  describe("DynSys1") {
    it("Can return the names of the variables in lexicographical order") {
      dynSys.namesVariables should be(TreeSet(VarName("x1"), VarName("x2")))
    }

    it("Can return the names of the parameters in lexicographical order") {
      dynSys.namesParams should be(TreeSet(ParamName("_w")))
    }

    it("Can return a Map[ConstName, Num] of the constants") {
      dynSys.constants should be(Map(ConstName("F") → 10.0, ConstName("MU") → 2.0, ConstName("OMEGA") → 2.0))
    }

    it("Can be evaluated with a proper input") {
      dynSys.eval(input) should be(Array(0.0, 0.0))
    }

    it("Can return the number of variables") {
      dynSys.numberVariables should be(2)
    }

    it("Can return the number of parameters") {
      dynSys.numberParams should be(1)
    }

    it("Can return the number of norms") {
      dynSys.numberNorms should be(1)
    }

    it("Can evaluate the norms with a proper input") {
      import DynSys1.x1
      val n = x1 * x1
      dynSys.evalNorms(input) should be(Array(n))
    }

  }
}
