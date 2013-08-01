package umich.plot

import org.scalatest.FunSuite

import org.jzy3d.analysis.AnalysisLauncher
import org.jzy3d.colors.Color

import umich.plot.tagobjects.PlotTest

class ScatterPlot3dTest extends FunSuite {
  test("Scatter 3D plots", PlotTest) {
    val sols = Seq(Array(1.0, 2.0, 3.0),
      Array(0.0, 4.0, 2.0),
      Array(2.5, 3.14, 2.7128))
    val colors = Array.fill(3)(Color.BLUE)
    try {
      AnalysisLauncher.open(ScatterPlot3d(sols, colors))
    } catch {
      case ex: Throwable ⇒ ex.printStackTrace()
    }
  }
}
