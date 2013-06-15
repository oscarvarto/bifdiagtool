package umich.plot

import org.scalatest.{ FunSuite, Matchers }

class PlotTest extends FunSuite with Matchers {
  test("2D plots") {
    import scalax.chart._
    import scalax.chart.Charting._
    import org.jfree.chart.renderer.xy.XYSplineRenderer

    val dataSet = Seq(
      (2.0, 56.27),
      (3.0, 41.32),
      (4.0, 31.45),
      (5.0, 30.05),
      (6.0, 24.69),
      (7.0, 19.78),
      (8.0, 20.94),
      (9.0, 16.73),
      (10.0, 14.21),
      (11.0, 12.44)).toXYSeriesCollection("some points")
    val chart = XYLineChart(dataSet, tooltips = true)
    import java.awt.Color
    import java.awt.Color._
    import collection.immutable.IndexedSeq
    val colors: Map[Int, Color] = (0 to 9).zip(
      IndexedSeq(BLACK, RED, BLUE, BLACK, RED, BLUE, BLACK, RED, BLUE, BLACK))
      .toMap
    val renderer = new XYSplineRenderer() {
      override def getItemPaint(series: Int, item: Int): java.awt.Paint = {
        colors(item)
      }
    }
    chart.plot.setRenderer(renderer)

    val saddle = "SaddleNode"
    val sink = "Sink"
    val source = "Source"
    val spiralSink = "SpiralSink"
    val spiralSource = "SpiralSource"
    val spiralSaddle = "SpiralSaddle"

    val tooltips: Map[Int, String] = (0 to 9).zip(
      IndexedSeq(saddle, sink, source, spiralSink, spiralSource, spiralSaddle,
        saddle, sink, source, spiralSink)).toMap
    val tooltipsF: (XYDataset, Int, Int) ⇒ String = { (dataset, series, item) ⇒
      import scalaz.syntax.std.option._
      tooltips.get(item).cata(identity, "Error")
    }
    chart.tooltipGenerator = tooltipsF
    import java.awt.Shape
    import java.awt.geom.Ellipse2D
    val shape = new Ellipse2D.Double(-4.0, -4.0, 8.0, 8.0)
    renderer.setSeriesShape(0, shape)
    chart.show
  }

  test("3D plots") {
    pending
  }
}
