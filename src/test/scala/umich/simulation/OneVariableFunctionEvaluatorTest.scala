package umich.simulation

import org.scalatest.{ FunSuite, GivenWhenThen, Matchers }

class OneVariableFunctionEvaluatorTest extends FunSuite
  with Matchers with GivenWhenThen {

  import umich.stability.NumDiff.rid
  import umich.parser.Names._

  test("x' = _r + x^2") {
    Given("The OneVariableFunctionEvaluator for the saddleNodeProject object")
    val fEv = OneVariableFunctionEvaluator(saddleNodeProject)

    When("-4.0 is the _r value, we get a function")
    val f = fEv.f(Map(ParamName("_r") → -4.0))
    Then("It is possible to evaluate the function")
    f(2.0) should be(0.0)
    f(-2.0) should be(0.0)
    And("It is possible to evaluate it's derivative calling ridders")
    import scalaz.syntax.std.option._
    val step: Double = saddleNodeProject.paramConfs.get(ParamName("_r"))
      .err("Test error").step
    val df = rid(step)(f(_))
    // df(2.0) > 0, then (r, x) = (-4.0, 2.0) must be an unstable fixed point
    df(2.0) should be(4.0 +- 1e-5)
    // df(-2.0) < 0, then (r, x) = (-4.0, -2.0) must be a stable fixed point
    df(-2.0) should be(-4.0 +- 1e-5)

    When("0.0 is the _r value, we get another function")
    val g = fEv.f(Map(ParamName("_r") → 0.0))
    Then("It is possible to evaluate the function")
    g(0.0) should be(0.0)
    And("It is possible to evaluate it's derivative calling ridders")
    val dg = rid(step)(g(_))
    // dg(0.0) == 0, then (r, x) = (0.0, 0.0) we cannot say anything about
    // the stability of this fixed point
    // However a graphical analysis of the bifurcation diagram
    // shows it is a Saddle Node.
    dg(0.0) should be(0.0 +- 1e-5)
  }

  test("x' = _r*x - x^2") {
    Given("The OneVariableFunctionEvaluator for the transcriticalBifProject object")
    val fEv = OneVariableFunctionEvaluator(transcriticalBifProject)
    When("1.0 is the _r value, we get one funtion")
    val f = fEv.f(Map(ParamName("_r") → 1.0))
    Then("It is possible to evaluate the function")
    f(1.0) should be(0.0)
    f(0.0) should be(0.0)
    And("It is possible to evaluate it's derivative calling ridders")
    import scalaz.syntax.std.option._
    val step: Double = transcriticalBifProject.paramConfs.get(ParamName("_r")).err("Test error").step
    val df = rid(step)(f(_))
    // df(1.0) < 0, then (r, x) = (1.0, 1.0) must be a stable fixed point
    df(1.0) should be(-1.0 +- 1e-5)
    // df(1.0) > 0, then (r, x) = (1.0, 0.0) must be an unstable fixed point
    df(0.0) should be(1.0 +- 1e-5)

    When("0.0 is the _r value, we get another function")
    val g = fEv.f(Map(ParamName("_r") → 0.0))
    Then("It is possible to evaluate the function")
    g(0.0) should be(0.0)
    And("It is possible to evaluate it's derivative calling ridders")
    val dg = rid(step)(g(_))
    // dg(0.0) == 0, then (r, x) = (0.0, 0.0) we cannot say anything about
    // the stability of this fixed point.
    // However a graphical analysis of the bifurcation diagram
    // shows it is a Transcritical Bifurcation Node.
    dg(0.0) should be(0.0 +- 1e-5)
  }

  test("x' = _r*x + x^3 - x^5") {
    Given("The OneVariableFunctionEvaluator for the subcriticalPitchforkBifProject object")
    val fEv = OneVariableFunctionEvaluator(subcriticalPitchforkBifProject)
    When("1.0 is the _r value, we get one funtion")
    val f = fEv.f(Map(ParamName("_r") → -0.25))
    Then("It is possible to evaluate the function")
    f(0.70710678) should be(0.0 +- 1e-6)
    f(-0.70710678) should be(0.0 +- 1e-6)
    And("It is possible to evaluate it's derivative calling ridders")
    import scalaz.syntax.std.option._
    val step: Double = transcriticalBifProject.paramConfs.get(ParamName("_r")).err("Test error").step
    val df = rid(step)(f(_))
    // df(0.70710678) = 0, then (r, x) = (-0.25, 0.70710678) has to be analized graphically
    // (It's a saddle node bifurcation)
    df(0.70710678) should be(0.0 +- 1e-5)
    // df(-0.70710678) = 0, then (r, x) = (-0.25, -0.70710678) has to be analized graphically
    // (but it's a saddle node bifurcation)
    df(-0.70710678) should be(0.0 +- 1e-5)

    When("0.0 is the _r value, we get another function")
    val g = fEv.f(Map(ParamName("_r") → 0.0))
    Then("It is possible to evaluate the function")
    g(0.0) should be(0.0)
    And("It is possible to evaluate it's derivative calling ridders")
    val dg = rid(step)(g(_))
    // dg(0.0) == 0, then (r, x) = (0.0, 0.0) we cannot say anything about
    // the stability of this fixed point.
    // However a graphical analysis of the bifurcation diagram
    // shows it is a Subcritical Pitchfork Bifurcation.
    dg(0.0) should be(0.0 +- 1e-5)
  }
}
