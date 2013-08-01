package umich.plot

import org.scalatest.{ FunSuite, GivenWhenThen, Matchers }

import umich.plot.tagobjects.PlotTest

class StabilityPlot3dTest extends FunSuite with Matchers with GivenWhenThen {
  test("Stability classification plot for insectOutbreakProject", PlotTest) {
    Given("Insect Outbreak Project")
    import spire.algebra._
    import spire.math._
    import spire.implicits._

    val r = { (x: Double) ⇒ 2 * x.pow(3) / (1 + x.pow(2)).pow(2) }
    // The restriction k > 0, implies x > 1
    val k = { (x: Double) ⇒ 2 * x.pow(3) / (x.pow(2) - 1.0) }

    import umich.stability._
    import umich.simulation.{ insectOutbreakProject, OneVariableFunctionEvaluator }
    import umich.parser.Names._

    info("""Classifying the bifurcation point for (r(x), k(x)) for x = 10.0.
    See Strogatz, section 3.7, page 77, eqs (8-9)""".stripMargin)
    val fEv = OneVariableFunctionEvaluator(insectOutbreakProject)
    val stabClas1 = stabilityClassifier.oneVariableStability(0.1)(
      fEv,
      Map(ParamName("_r") → r(10.0), ParamName("_k") → k(10.0)))
    stabClas1(10.0) should be(Undefined)

    info("""Solutions used in this test for the 3d Plot were calculated with Sage 5.9, using the following code:
    var('r k')
    eq = r*(k - x)*(1 + x^2) - k*x
    sols = eq.roots(x, multiplicities=false)
    eq2 = r*x*(1 - x/k) - x^2/(1 + x^2)
    deq2 = diff(eq2,x)
    total, total_real, good_count = 0, 0, 0
    for sol in sols:
        ss = [[r0, k0, sol.subs(r = r0, k = k0).N()] for r0 in [0.05*i for i in range(1, 11)] for k0 in [10.0*j for j in range(1, 4)]]
        print "*"*50
        total += len(ss)
        for i, s in enumerate(ss):
            if s[2].is_real():
                total_real += 1
                error = eq2.subs(r = s[0], k = s[1], x = s[2])
                if abs(error) < 1.0e-6:
                    good_count += 1
                    print i, s[0].N(digits=2), s[1].N(digits=2), s[2], deq2.subs(r = s[0], k = s[1], x = s[2]).N()

    print "="*50
    print "Total", total
    print "Total real", total_real
    print "Good count", good_count
    """.stripMargin)

    val sols: Seq[Array[Double]] = Seq(
      Array(0.050, 10.0, 0.0498743804879176),
      Array(0.050, 20.0, 0.0499996867226855),
      Array(0.050, 30.0, 0.0500415965546281),
      Array(0.10, 10.0, 0.0999899020598702),
      Array(0.10, 20.0, 0.100502486798868),
      Array(0.10, 30.0, 0.100674552347680),
      Array(0.15, 10.0, 0.151106628810168),
      Array(0.15, 20.0, 0.152310962290672),
      Array(0.15, 30.0, 20.0493866091018),
      Array(0.20, 10.0, 0.204078019024402),
      Array(0.20, 20.0, 10.9125523560141),
      Array(0.20, 30.0, 23.6756627401014),
      Array(0.25, 10.0, 0.259956226117594),
      Array(0.30, 10.0, 0.320161667256341),
      Array(0.35, 20.0, 16.5623699612230))

    sols.foreach { a ⇒
      a match {
        case Array(r, k, x) ⇒
          val f = fEv.f(Map(ParamName("_r") → r, ParamName("_k") → k))
          info(s"Test that ($r, $k, $x) is a fixed point")
          f(x) should be(0.0 +- 1e-5)
          val stabClas = stabilityClassifier.oneVariableStability(0.1)(
            fEv,
            Map(ParamName("_r") → r, ParamName("_k") → k))
          info(s"Test that ($r, $k, $x) is AsymptoticallyStable")
          stabClas(x) should be(AsymptoticallyStable)
      }
    }
    info("Every fixed point should have the color corresponding to AsymptoticallyStable")
    plotFixedPoints.plot3d(insectOutbreakProject, sols)
  }
}
