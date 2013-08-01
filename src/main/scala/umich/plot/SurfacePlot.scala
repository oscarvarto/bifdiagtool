package umich.plot

import org.jzy3d.analysis.AbstractAnalysis
import org.jzy3d.chart.factories.AWTChartComponentFactory
import org.jzy3d.colors.ColorMapper
import org.jzy3d.colors.colormaps.ColorMapRainbow
import org.jzy3d.maths.Range
import org.jzy3d.plot3d.builder.{ Builder, Mapper }
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid
import org.jzy3d.plot3d.primitives.Shape
import org.jzy3d.plot3d.rendering.canvas.Quality

case class SurfacePlot(mapper: Mapper, range: Range, steps: Int) extends AbstractAnalysis {
  override def init() {
    // Create the object to represent the function over the given range
    val surface: Shape = Builder.buildOrthonormal(new OrthonormalGrid(
      range, steps, range, steps), mapper).asInstanceOf[Shape]
    surface.setColorMapper(new ColorMapper(
      new ColorMapRainbow, surface.getBounds.getZmin, surface.getBounds.getZmax))
    surface.setFaceDisplayed(true)
    surface.setWireframeDisplayed(false)

    // Create chart
    chart = AWTChartComponentFactory.chart(Quality.Advanced, getCanvasType)
    chart.getScene.getGraph.add(surface)
  }
}
