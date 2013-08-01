package umich.stability

import java.awt.Color

// NOTE to maintainer: If these objects are not going to be used in pattern matching,
// maybe you should remove "case" from them.
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
    val dfx = df(x)
    val tol = 1e-8
    if (math.abs(dfx) <= tol)
      Undefined
    else if (dfx > tol)
      Unstable
    else
      AsymptoticallyStable
  }

  import org.ejml.data.Complex64F
  private def stabilityOrLinearConjugateFlow(eigVals: Array[Complex64F]): Either[Stability, LinearConjugateFlow] = {
    case class eigInfo(
      val isHyperbolic: Boolean,
      val negativeFound: Boolean,
      val positiveFound: Boolean,
      val complexFound: Boolean)

    val eigInfo0 = eigInfo(
      isHyperbolic = true,
      negativeFound = false,
      positiveFound = false,
      complexFound = false)

    val tol = 1e-8
    import math.abs
    val finalInfo = eigVals.foldLeft(eigInfo0)((info, complexN) ⇒ {
      val realPart = complexN.getReal()
      val imagPart = complexN.getImaginary()
      val isHyperbolic = info.isHyperbolic && abs(realPart) >= tol // compare with 0.0 ??? doesn't seem OK
      val negativeFound = info.negativeFound || realPart < -tol
      val positiveFound = info.positiveFound || realPart > tol
      val complexFound = info.complexFound || abs(imagPart) >= tol
      eigInfo(
        isHyperbolic,
        negativeFound,
        positiveFound,
        complexFound)
    })

    val classification: Either[Stability, LinearConjugateFlow] = finalInfo match {
      case eigInfo(false, false, false, _)   ⇒ Left(Undefined) //  1
      case eigInfo(false, false, true, _)    ⇒ Left(Unstable) //  2
      case eigInfo(false, true, false, _)    ⇒ Left(Undefined) //  3
      case eigInfo(false, true, true, _)     ⇒ Left(Unstable) //  4
      case eigInfo(true, false, true, false) ⇒ Right(Source) //  5
      case eigInfo(true, false, true, true)  ⇒ Right(SpiralSource) //  6
      case eigInfo(true, true, false, false) ⇒ Right(Sink) //  7
      case eigInfo(true, true, false, true)  ⇒ Right(SpiralSink) //  8
      case eigInfo(true, true, true, false)  ⇒ Right(SaddleNode) //  9
      case eigInfo(true, true, true, true)   ⇒ Right(SpiralSaddle) // 10
    }
    classification
  }

  def classify(mData: Array[Array[Double]]): Option[Either[Stability, LinearConjugateFlow]] = {
    require(mData.size == mData(0).size)
    import org.ejml.data.DenseMatrix64F
    import org.ejml.alg.dense.decomposition.eig.SwitchingEigenDecomposition

    val m = new DenseMatrix64F(mData)
    val n = m.numRows
    val eigDec = new SwitchingEigenDecomposition(n)
    import scalaz.std.option._
    import scalaz.syntax.std.boolean._
    import spire.syntax.cfor._
    eigDec.decompose(m).fold(
      {
        val eigVals = Array.ofDim[Complex64F](n)
        cfor(0)(_ < n, _ + 1) { i ⇒
          eigVals(i) = (eigDec.getEigenvalue(i))
        }
        some(stabilityOrLinearConjugateFlow(eigVals))
      },
      None)
  }
}
