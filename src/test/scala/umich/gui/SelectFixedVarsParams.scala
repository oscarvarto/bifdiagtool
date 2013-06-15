package umich.gui

import scala.collection.immutable.TreeSet

import org.scalatest.{ FunSuite, GivenWhenThen }
import org.scalatest.Matchers

import umich.parser.Names._

class SelectFixedVarsParams extends FunSuite
  with Matchers with GivenWhenThen {
  test("Selecting POSSIBLE fixed variables and REQUIRED fixed parameters ") {
    Given("Variables and parameters of a dynamical system")
    val namesVariables = TreeSet(VarName("x"))
    val namesParams = TreeSet(ParamName("_a"), ParamName("_b"))

    When("You know which variables are in norms and which parameters will " +
      " vary in a given range")
    val namesVariablesInNorm = TreeSet(VarName("x"))
    val paramsChanging = TreeSet(ParamName("_a"))

    Then("You can tell which variables COULD be fixed " +
      "and which parameters MUST be fixed")
    val possibleFixedVariables = (namesVariables diff namesVariablesInNorm).toList
    possibleFixedVariables should be(List.empty[VarName])
    val fixedParameters = (namesParams diff paramsChanging).toList
    fixedParameters should be(List(ParamName("_b")))
  }
}
