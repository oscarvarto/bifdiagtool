package umich.simulation

import org.scalatest.FunSuite
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

class ProjectTest extends FunSuite with Matchers with GivenWhenThen {
  import umich.parser.DynSys2.dynSys
  import umich.parser.Names._

  test("Two parameter configuration can be added to a Project") {
    Given("A project with no parameter configuration")
    val proj = Project("Dummy Project", dynSys)

    When("A two parameter configuration are added")
    import scalaz.ValidationNel

    val parConf1: ValidationNel[String, ParameterConfig] = ParameterConfig(-1.0, 1.0, 0.1)
    val kv1 = (ParamName("_a") → parConf1.toOption.get)
    val parConf2: ValidationNel[String, ParameterConfig] = ParameterConfig(5.0, 10.0, 0.2)
    val kv2 = (ParamName("_b") → parConf2.toOption.get)
    val projWithConfs = proj.addParamConf(kv1).addParamConf(kv2)

    Then("The number of parameter configurations should be two")
    projWithConfs.paramConfs.size should be(2)

    And("The Project should contain the added configurations")
    import scala.collection.immutable.TreeMap
    projWithConfs.paramConfs should be(TreeMap(kv1, kv2))
  }

  test("A simulation can be started on a given Parameter Configuration") {
    pending
  }

  test("A project can add some fixed values") {
    Given("A project with no fixed values")
    val proj = Project("Dummy", dynSys)

    When("A parameter and a variable are fixed and added")
    val proj1 = proj.addFixedValue(ParamName("_a") → 2.0).addFixedValue(VarName("x") → 0.5)

    Then("fixedValues should contain added values")
    import shapeless._
    proj1.fixedValues should be(Map(VarName("x") → 0.5) :: Map(ParamName("_a") → 2.0) :: HNil)
  }
}
