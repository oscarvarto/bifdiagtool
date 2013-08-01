package umich.plot

import org.scalatest.FunSuite

import org.jzy3d.analysis.AnalysisLauncher
import org.jzy3d.plot3d.builder.Mapper
import org.jzy3d.maths.Range

import umich.plot.tagobjects.PlotTest

class AckleyTest extends FunSuite {
  test("Ackley with two variables", PlotTest) {
    import math._
    val mapper: Mapper = (x: Double, y: Double) ⇒ {
      val exponent1 = -0.2 * sqrt((x * x + y * y) / 2.0)
      val exponent2 = (cos(2 * Pi * x) + cos(2 * Pi * y)) / 2.0
      val z = -20.0 * exp(exponent1) - exp(exponent2) + 20 + E
      z
    }
    val range = new Range(-30.0, 30.0)
    val steps = 80
    try {
      AnalysisLauncher.open(SurfacePlot(mapper, range, steps))
    } catch {
      case ex: Throwable ⇒ ex.printStackTrace()
    }
  }
}
