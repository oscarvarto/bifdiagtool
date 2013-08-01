package umich.plot

import org.scalatest.FunSuite

import org.jzy3d.analysis.AnalysisLauncher
import org.jzy3d.plot3d.builder.Mapper
import org.jzy3d.maths.Range

import umich.plot.tagobjects.PlotTest

class GriewankTest extends FunSuite {
  test("Easom with two variables", PlotTest) {
    import math._
    val sqrt2 = sqrt(2.0)
    val mapper: Mapper = (x: Double, y: Double) ⇒ {
      val term1 = (x * x + y * y) / 4000.0
      val term2 = cos(x) * cos(y / sqrt2)
      val z = 1 + term1 - term2
      z
    }
    val range = new Range(-10.0, 10.0)
    val steps = 80
    try {
      AnalysisLauncher.open(SurfacePlot(mapper, range, steps))
    } catch {
      case ex: Throwable ⇒ ex.printStackTrace()
    }
  }
}
