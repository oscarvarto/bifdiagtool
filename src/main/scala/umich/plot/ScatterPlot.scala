package umich.plot

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;

// Being more correct, we should make
// sols: Seq[Sized[Array[Double], _3]]
case class ScatterPlot3d(sols: Seq[Array[Double]], colors: Array[Color]) extends AbstractAnalysis {
  override def init() {
    require(sols.length == colors.length)
    val fixedPoints = sols.map { sol ⇒
      sol match {
        case Array(r, k, norm) ⇒ new Coord3d(r, k, norm)
      }
    }.toArray
    val scatter = new Scatter(fixedPoints, colors, 5.0f)
    chart = AWTChartComponentFactory.chart(Quality.Advanced, getCanvasType)
    chart.getScene().add(scatter)
  }
}
