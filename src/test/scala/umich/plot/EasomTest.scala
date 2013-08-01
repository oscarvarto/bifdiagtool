package umich.plot

import org.scalatest.FunSuite

import org.jzy3d.analysis.AnalysisLauncher
import org.jzy3d.plot3d.builder.Mapper
import org.jzy3d.maths.Range

import umich.plot.tagobjects.PlotTest

class EasomTest extends FunSuite {
  test("Easom with two variables", PlotTest) {
    import math._
    val mapper: Mapper = (x: Double, y: Double) ⇒ {
      val term1 = x - Pi
      val term2 = y - Pi
      val exponent = -term1 * term1 - term2 * term2
      val z = -cos(x) * cos(y) * exp(exponent)
      z
    }
    val range = new Range(0.0, 6.2832)
    val steps = 80
    try {
      AnalysisLauncher.open(SurfacePlot(mapper, range, steps))
    } catch {
      case ex: Throwable ⇒ ex.printStackTrace()
    }
  }
}
