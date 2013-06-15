package umich.plot

import org.scalatest.{ FunSuite, GivenWhenThen, Matchers }

class SaddleNodeTest extends FunSuite with Matchers with GivenWhenThen {
  test("Testing stability classification of saddleNodeProject") {
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

    val rs: Array[Double] = Array.tabulate(51) { -0.1 * _ }
    val xP: Array[Double] = rs map { r ⇒ math.sqrt(-r) }
    val xN = xP map { -1.0 * _ }
    val sols = (rs ++ rs).zip(xP ++ xN).toSeq

    import scalax.chart._
    import scalax.chart.Charting._
    import org.jfree.chart.renderer.xy.XYShapeRenderer
    import java.awt.Color

    val dataSet = sols.toXYSeriesCollection("SaddleNode fixed points", autoSort = false)
    val colors: Map[Int, Color] = sols.zipWithIndex.map { t ⇒
      val (r, x) = t._1
      val stabClas = stabilityClassifier.oneVariableStability(0.01)(
        OneVariableFunctionEvaluator(saddleNodeProject),
        Map(ParamName("_r") → r))
      println(t + stabClas(x).color.toString)
      (t._2, stabClas(x).color)
    }.toMap

    val renderer = new XYShapeRenderer() {
      override def getItemPaint(series: Int, item: Int): java.awt.Paint = { colors(item) }
    }
    val chart = XYLineChart(dataSet)
    chart.plot.setRenderer(renderer)
    chart.show()
  }
}
