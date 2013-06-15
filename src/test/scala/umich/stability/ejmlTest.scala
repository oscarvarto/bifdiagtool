package umich.stability

import org.scalatest.{ FunSuite, Matchers }
import org.ejml.data.Complex64F

class EJMLTest extends FunSuite with Matchers {
  def stabilityOrLinearConjugateFlow(eigVals: Array[Complex64F]): Either[Stability, LinearConjugateFlow] = {
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

    val finalInfo = eigVals.foldLeft(eigInfo0)((info, complexN) ⇒ {
      val realPart = complexN.getReal()
      val imagPart = complexN.getImaginary()
      val isHyperbolic = info.isHyperbolic && realPart != 0.0 // compare with 0.0 ??? doesn't seem OK
      val negativeFound = info.negativeFound || realPart < 0.0
      val positiveFound = info.positiveFound || realPart > 0.0
      val complexFound = info.complexFound || imagPart != 0.0
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
    import spire.syntax._
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

  test("Case 1. Ideal examples") {
    val m11 = Array.fill(4, 4)(0.0)
    classify(m11) should be(Some(Left(Undefined)))

    val m12 = Array(
      Array(0.0, 1.0),
      Array(-1.0, 0.0))
    classify(m12) should be(Some(Left(Undefined)))

    val m13 = Array(
      Array(0.0, 1.0, 0.0, 0.0),
      Array(-1.0, 0.0, 0.0, 0.0),
      Array(0.0, 0.0, 0.0, -100.0),
      Array(0.0, 0.0, 100.0, 0.0))
    classify(m13) should be(Some(Left(Undefined)))
  }

  test("Case 2. Ideal examples") {
    val m21 = Array(
      Array(1.0, 0.0, 0.0),
      Array(0.0, 2.0, 0.0),
      Array(0.0, 0.0, 0.0))
    classify(m21) should be(Some(Left(Unstable)))

    val m22 = Array(
      Array(1.0, 3.0, 0.0),
      Array(-3.0, 1.0, 0.0),
      Array(0.0, 0.0, 0.0))
    classify(m22) should be(Some(Left(Unstable)))
  }

  test("Case 3. Ideal examples") {
    val m31 = Array(
      Array(0.0, 0.0, 0.0),
      Array(0.0, -1.0, 0.0),
      Array(0.0, 0.0, -100.0))
    classify(m31) should be(Some(Left(Undefined)))

    val m32 = Array(
      Array(-1.0, 3.0, 0.0, 0.0, 0.0),
      Array(-3.0, -1.0, 0.0, 0.0, 0.0),
      Array(0.0, 0.0, -1.0, 3.0, 0.0),
      Array(0.0, 0.0, -3.0, -1.0, 0.0),
      Array(0.0, 0.0, 0.0, 0.0, 0.0))
    classify(m32) should be(Some(Left(Undefined)))
  }

  test("Case 4. Ideal examples") {
    val m41 = Array(
      Array(-1.0, 0.0, 0.0),
      Array(0.0, 0.0, 0.0),
      Array(0.0, 0.0, 1.0))
    classify(m41) should be(Some(Left(Unstable)))

    val m42 = Array(
      Array(-1.0, 3.0, 0.0, 0.0, 0.0),
      Array(-3.0, -1.0, 0.0, 0.0, 0.0),
      Array(0.0, 0.0, 1.0, 3.0, 0.0),
      Array(0.0, 0.0, -3.0, 1.0, 0.0),
      Array(0.0, 0.0, 0.0, 0.0, 0.0))
    classify(m42) should be(Some(Left(Unstable)))
  }

  test("Case 5. Ideal examples") {
    val m51 = Array.tabulate(10, 10)((i, j) ⇒ if (i == j) i + 1.0 else 0.0)
    classify(m51) should be(Some(Right(Source)))
  }

  test("Case 6. Ideal examples") {
    val m61 = Array(
      Array(1.0, 3.0, 0.0, 0.0, 0.0),
      Array(-3.0, 1.0, 0.0, 0.0, 0.0),
      Array(0.0, 0.0, 1.0, 3.0, 0.0),
      Array(0.0, 0.0, -3.0, 1.0, 0.0),
      Array(0.0, 0.0, 0.0, 0.0, 5.0))
    classify(m61) should be(Some(Right(SpiralSource)))

    val m62 = Array(
      Array(1.0, 3.0, 0.0, 0.0, 0.0),
      Array(-3.0, 1.0, 0.0, 0.0, 0.0),
      Array(0.0, 0.0, 9.0, 3.0, 0.0),
      Array(0.0, 0.0, -3.0, 9.0, 0.0),
      Array(0.0, 0.0, 0.0, 0.0, 0.1))
    classify(m62) should be(Some(Right(SpiralSource)))
  }

  test("Case 7. Ideal examples") {
    val m71 = Array.tabulate(10, 10)((i, j) ⇒ if (i == j) -(i + 1.0) else 0.0)
    classify(m71) should be(Some(Right(Sink)))
  }

  test("Case 8. Ideal examples") {
    val m81 = Array(
      Array(-1.0, 3.0, 0.0, 0.0, 0.0),
      Array(-3.0, -1.0, 0.0, 0.0, 0.0),
      Array(0.0, 0.0, -1.0, 3.0, 0.0),
      Array(0.0, 0.0, -3.0, -1.0, 0.0),
      Array(0.0, 0.0, 0.0, 0.0, -5.0))
    classify(m81) should be(Some(Right(SpiralSink)))

    val m82 = Array(
      Array(-1.0, 3.0, 0.0, 0.0, 0.0),
      Array(-3.0, -1.0, 0.0, 0.0, 0.0),
      Array(0.0, 0.0, -9.0, 3.0, 0.0),
      Array(0.0, 0.0, -3.0, -9.0, 0.0),
      Array(0.0, 0.0, 0.0, 0.0, -0.1))
    classify(m82) should be(Some(Right(SpiralSink)))
  }

  test("Case 9. Ideal examples") {
    val m91 = Array(
      Array(-100.0, 0.0, 0.0),
      Array(0.0, 100.0, 0.0),
      Array(0.0, 0.0, 0.1))
    classify(m91) should be(Some(Right(SaddleNode)))
  }

  test("Case 10. Ideal examples") {
    val m101 = Array(
      Array(1.0, 3.0, 0.0, 0.0, 0.0),
      Array(-3.0, 1.0, 0.0, 0.0, 0.0),
      Array(0.0, 0.0, -1.0, 3.0, 0.0),
      Array(0.0, 0.0, -3.0, -1.0, 0.0),
      Array(0.0, 0.0, 0.0, 0.0, 5.0))
    classify(m101) should be(Some(Right(SpiralSaddle)))

    val m102 = Array(
      Array(1.0, 3.0, 0.0, 0.0, 0.0),
      Array(-3.0, 1.0, 0.0, 0.0, 0.0),
      Array(0.0, 0.0, -9.0, 3.0, 0.0),
      Array(0.0, 0.0, -3.0, -9.0, 0.0),
      Array(0.0, 0.0, 0.0, 0.0, -0.1))
    classify(m102) should be(Some(Right(SpiralSaddle)))
  }
}
