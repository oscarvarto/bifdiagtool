package umich.stability

import java.awt.Color

sealed trait Stability {
  def color: Color
}
case object AsymptoticallyStable extends Stability { // Liapunov Stable & Attracting
  val color = Color.BLUE
}

//case object NeutrallyStable extends Stability // Liapunov Stable & NOT Attracting
case object Unstable extends Stability { // NOT Liapunov Stable NOR Attracting
  val color = Color.RED
}
case object Undefined extends Stability {
  val color = Color.BLACK
}

sealed class LinearConjugateFlow(val stability: Stability)
case object SaddleNode extends LinearConjugateFlow(Unstable)
case object Sink extends LinearConjugateFlow(AsymptoticallyStable)
case object Source extends LinearConjugateFlow(Unstable)

// These are meaningful in the 2D case only
case object SpiralSink extends LinearConjugateFlow(AsymptoticallyStable)
//case class Center() extends EquilibriumPoint(NeutrallyStable)
case object SpiralSource extends LinearConjugateFlow(Unstable)
case object SpiralSaddle extends LinearConjugateFlow(Unstable)

import umich.simulation.OneVariableFunctionEvaluator
import NumDiff.rid
import umich.parser.Names._

object stabilityClassifier {
  def oneVariableStability(h: Double)(fnEv: OneVariableFunctionEvaluator,
                                      closedParams: Map[ParamName, Double]): Double ⇒ Stability = { x ⇒
    val f = fnEv.f(closedParams)
    val df = rid(h)(f)
    import math.signum
    signum(df(x)) match {
      case -1.0 ⇒ AsymptoticallyStable
      case 0.0  ⇒ Undefined
      case 1.0  ⇒ Unstable
    }
  }
}
