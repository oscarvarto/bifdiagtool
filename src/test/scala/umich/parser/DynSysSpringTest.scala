package umich.parser

import org.scalatest.{ FunSpec, Matchers }
import scala.collection.immutable.TreeSet

class DynSysSpringTest extends FunSpec with Matchers {
  import DynSysSpring.dynSys
  import DynSysSpring.input
  import Names._

  describe("DynSysSpring") {
    it("Can return the names of the variables in lexicographical order") {
      dynSys.namesVariables should be(TreeSet(VarName("x"), VarName("v")))
    }

    it("Can return the names of the parameters in lexicographical order") {
      dynSys.namesParams should be(TreeSet(ParamName("_c"), ParamName("_w0")))
    }

    it("Can return a Map[ConstName, Num] of the constants") {
      dynSys.constants should be(Map.empty)
    }

    it("Can be evaluated with a proper input") {
      dynSys.eval(input) should be(Array(0.0, 0.0))
    }

    it("Can return the number of variables") {
      dynSys.numberVariables should be(2)
    }

    it("Can return the number of parameters") {
      dynSys.numberParams should be(2)
    }

    it("Can return the number of norms") {
      dynSys.numberNorms should be(1)
    }

    it("Can evaluate the norms with a proper input") {
      dynSys.evalNorms(input) should be(Array(0.0))
    }

    it("Determines if a simulation could be run with two parameters or only one") {
      dynSys.maybeTwoParameterSimulation should be(true)
    }
  }
}
