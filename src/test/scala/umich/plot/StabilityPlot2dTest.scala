package umich.plot

import org.scalatest.{ FunSuite, GivenWhenThen, Matchers }

import umich.plot.tagobjects.PlotTest

class StabilityPlot2dTest extends FunSuite with Matchers with GivenWhenThen {
  test("Stability classification plot of saddleNodeProject", PlotTest) {
    Given("Saddle Node Project")
    import umich.stability._
    import umich.simulation.{ OneVariableFunctionEvaluator, saddleNodeProject }
    import umich.parser.Names._

    info("Classifying (r, x) = (-4.0, 2.0)")
    val stabClas1 = stabilityClassifier.oneVariableStability(0.1)(
      OneVariableFunctionEvaluator(saddleNodeProject),
      Map(ParamName("_r") → -4.0))
    stabClas1(2.0) should be(Unstable)
    info("Classifying (r, x) = (-4.0, -2.0)")
    stabClas1(-2.0) should be(AsymptoticallyStable)

    val stabClas2 = stabilityClassifier.oneVariableStability(0.1)(
      OneVariableFunctionEvaluator(saddleNodeProject),
      Map(ParamName("_r") → 0.0))
    info("Classifying (r, x) = (0.0, 0.0)")
    stabClas2(0.0) should be(Undefined)

    val rs = Array.tabulate[Double](51) { -0.1 * _ }
    val xP = rs map { r ⇒ math.sqrt(-r) }
    val xN = xP map { -1.0 * _ }
    val sols = (rs ++ rs).zip(xP ++ xN).toSeq

    val chart = plotFixedPoints.plot2d(saddleNodeProject, sols, "SaddleNode fixed points")
    chart.show()
  }

  test("Stability classification plot of transcriticalBifProject", PlotTest) {
    Given("transcriticalBifProject")
    import umich.simulation.transcriticalBifProject
    val rs = Array.tabulate[Double](101) { -5.0 + 0.1 * _ }
    val xUp = Array.fill(51)(0.0) ++ Array.tabulate[Double](50) { 0.1 * _ }
    val xDown = rs.take(51) ++ Array.fill(50)(0.0)
    val sols = (rs ++ rs).zip(xUp ++ xDown).toSeq
    val chart = plotFixedPoints.plot2d(transcriticalBifProject, sols, "Transcritical Bifurcation")
    chart.show()
  }

  test("Stability classification plot of subcriticalPitchforkBifProject", PlotTest) {
    Given("subcriticalPitchforkBifProject")
    import umich.simulation.subcriticalPitchforkBifProject

    import spire.algebra._ // provides algebraic type classes
    import spire.math._ // provides functions, types, and type classes
    import spire.implicits._ // provides infix operators, instances and conversions

    val r0 = Array.tabulate(56) { -0.30 + 0.01 * _ }
    val x0 = Array.fill(56)(0.0)
    val r1 = Array.tabulate[Double](51) { -0.25 + 0.01 * _ }
    val x1StableUp = r1 map { r ⇒ (0.5 * (4 * r + 1).sqrt + 0.5).sqrt }
    val x1StableDown = -x1StableUp
    val r2 = r1.take(26)
    val x2UnstableUp = r2 map { r ⇒ (-0.5 * (4 * r + 1).sqrt + 0.5).sqrt }
    val x2UnstableDown = -x2UnstableUp
    val sols = (r0 ++ r1 ++ r1 ++ r2 ++ r2).zip(x0 ++ x1StableUp ++ x1StableDown ++ x2UnstableUp ++ x2UnstableDown).toSeq
    val chart = plotFixedPoints.plot2d(subcriticalPitchforkBifProject, sols, "Subcritical Pitchfork Bifurcation")
    chart.show()
  }
}
