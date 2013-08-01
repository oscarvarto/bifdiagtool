package umich.plot

import javax.swing.JPanel

import umich.stability._
import umich.simulation.OneVariableFunctionEvaluator
import umich.simulation.Project

object plotFixedPoints {
  import scalax.chart.XYChart
  // This method has to be modified to plot Norm1 vs parameter,
  // Not VarName vs parameter, to be more general and fit in the proposed model
  def plot2d(proj: Project, sols: Seq[(Double, Double)], title: String): XYChart = {
    import scalax.chart._
    import scalax.chart.Charting._
    import org.jfree.chart.renderer.xy.XYShapeRenderer
    import java.awt.Color

    val dataSet = sols.toXYSeriesCollection(title, autoSort = false)
    // This method is requiring paramConfs has exactly one ParamName
    // See also another way to do it in implementation of OneVariableFunctionEvaluator.f
    val fParamName = proj.paramConfs.keySet.head
    val colors: Map[Int, Color] = sols.zipWithIndex.map { t ⇒
      val (r, x) = t._1
      val stabClas = stabilityClassifier.oneVariableStability(0.01)(
        OneVariableFunctionEvaluator(proj),
        Map(fParamName → r))
      (t._2, stabClas(x).color)
    }.toMap
    val renderer = new XYShapeRenderer() {
      override def getItemPaint(series: Int, item: Int): java.awt.Paint = colors(item)
    }
    // TODO: instead of plotting the original dataSet, we should get a new one
    // using sols to get
    // a plot of Norm1 Vs fParamName
    val chart = XYLineChart(dataSet)
    chart.plot.setRenderer(renderer)
    chart
  }

  // Same comment about plotting Norms vs parameters. See plot2d comment above.
  // sols should be Seq[Sized[Array[Double], _3]])
  def plot3d(proj: Project, sols: Seq[Array[Double]]) {
    import org.jzy3d.colors.Color
    // This happens if
    // + More than one parameter varying <-- Insect Outbreak is a practical use case
    // + One parameter varying and some other fixed <-- Occurs for practical use cases? Don't think so.
    if (proj.dynSys.numberVariables == 1 && proj.dynSys.numberNorms == 1) {
      val colors: Array[Color] = sols.map { sol ⇒
        // Should be sol.unsized match { ... }
        sol match {
          case Array(r, k, x) ⇒
            val paramNames = proj.paramConfs.keySet.toIndexedSeq
            // For this use case, we are assuming there are two parameter values in each solution
            // Is there any other use case in practice?
            // Parameter, variable, norm? Only if norm is not just the variable...
            require(paramNames.size == 2) // too restrictive/short sighted?
            val par1 = paramNames(0)
            val par2 = paramNames(1)
            val stabClas = stabilityClassifier.oneVariableStability(0.01)(
              OneVariableFunctionEvaluator(proj),
              Map(par1 → r, par2 → k))
            val colorComps = Array.fill[Float](3)(0.0f)
            // It is Ok to use x (the VarName value) to get the color
            stabClas(x).color.getRGBColorComponents(colorComps)
            new Color(colorComps(0), colorComps(1), colorComps(2))
        }
      }.toArray
      import org.jzy3d.analysis.AnalysisLauncher

      // TODO: instead of plotting sols, we should map sols to get Norm vs paramNames
      try {
        AnalysisLauncher.open(ScatterPlot3d(sols, colors))
      } catch {
        case ex: Throwable ⇒ ex.printStackTrace()
      }
    }
  }
}
