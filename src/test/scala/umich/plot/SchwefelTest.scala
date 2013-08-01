package umich.plot

import org.scalatest.FunSuite

import org.jzy3d.analysis.AnalysisLauncher
import org.jzy3d.plot3d.builder.Mapper
import org.jzy3d.maths.Range

import umich.plot.tagobjects.PlotTest

class SchwefelTest extends FunSuite {
  test("Schwefel with 2 variables", PlotTest) {
    import math._
    import scalaz._, Scalaz._
    val steps = 80
    val f = (sin _ ⋘ sqrt ⋘ abs)
    val mapper: Mapper = { (x: Double, y: Double) ⇒ x * f(x) + y * f(y) + 837.9658 }
    // Define range and precision for the function to plot
    val range = new Range(-500, 500.0)
    try {
      AnalysisLauncher.open(SurfacePlot(mapper, range, steps))
    } catch {
      case ex: Throwable ⇒ ex.printStackTrace()
    }
  }
}
