package umich.stability

import org.scalatest.{ FunSuite, GivenWhenThen, Matchers }
//import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.EigenDecomposition

// This is a quick and dirty test. Needs some refactoring.

class ApacheCommonsMathTest extends FunSuite with Matchers with GivenWhenThen {
  test("Testing eigenvalue decomposition for small matrices") {
    val matrixData1 = Array(
      Array(-4.0, -4.0),
      Array(1.0, 0.0))
    val m1 = MatrixUtils.createRealMatrix(matrixData1)
    val eigDec1 = new EigenDecomposition(m1)
    eigDec1.hasComplexEigenvalues() should be(false)

    val matrixData2 = Array(
      Array(2.0, 3.0),
      Array(-3.0, -4.0))
    val m2 = MatrixUtils.createRealMatrix(matrixData2)
    val eigDec2 = new EigenDecomposition(m2)
    eigDec2.hasComplexEigenvalues() should be(false)

    import org.ejml.alg.dense.decomposition.eig.EigenvalueSmall
    val eigDec3 = new EigenvalueSmall()
    eigDec3.value2x2(2.0, 3.0, -3.0, -4.0)
    eigDec3.value0.isReal() should be(true)
    eigDec3.value1.isReal() should be(true)

    import org.ejml.data.DenseMatrix64F
    import org.ejml.alg.dense.decomposition.eig.SwitchingEigenDecomposition
    import org.ejml.data.Complex64F

    val m4 = new DenseMatrix64F(matrixData2)
    val eigDec4 = new SwitchingEigenDecomposition(2)
    import scalaz.std.option._
    import scalaz.syntax.std.boolean._
    import spire.syntax.cfor._
    eigDec4.decompose(m4).fold(
      {
        val eigVals = Array.ofDim[Complex64F](2)
        cfor(0)(_ < 2, _ + 1) { i ⇒
          eigVals(i) = eigDec4.getEigenvalue(i)
          println(eigVals(i))
          eigVals(i).isReal() should be(true)
        }
        some(eigVals)
      },
      None)

    val matrixData5 = Array(
      Array(1.0, -2.0, -2.0),
      Array(-2.0, 2.0, 3.0),
      Array(2.0, -3.0, -4.0))
    val m5 = MatrixUtils.createRealMatrix(matrixData5)
    val eigDec5 = new EigenDecomposition(m5)
    eigDec5.hasComplexEigenvalues() should be(false)
    val D5 = eigDec5.getD()
    println(D5)

    val m5b = new DenseMatrix64F(matrixData5)
    val eigDec5b = new SwitchingEigenDecomposition(3)
    eigDec5b.decompose(m5b).fold(
      {
        val eigVals = Array.ofDim[Complex64F](3)
        cfor(0)(_ < 3, _ + 1) { i ⇒
          eigVals(i) = eigDec5b.getEigenvalue(i)
          println(eigVals(i))
          eigVals(i).isReal() should be(true)
        }
        some(eigVals)
      },
      None)

    val matrixData6 = Array(
      Array(0.0, 1.0, 0.0),
      Array(-1.0, 2.0, 0.0),
      Array(0.0, 0.0, 1.0))
    val m6 = MatrixUtils.createRealMatrix(matrixData6)
    val eigDec6 = new EigenDecomposition(m6)
    eigDec6.hasComplexEigenvalues() should be(false)
    val D6 = eigDec6.getD()
    println(D6)

    val m6b = new DenseMatrix64F(matrixData6)
    val eigDec6b = new SwitchingEigenDecomposition(3)
    eigDec6b.decompose(m6b).fold(
      {
        val eigVals = Array.ofDim[Complex64F](3)
        cfor(0)(_ < 3, _ + 1) { i ⇒
          eigVals(i) = eigDec6b.getEigenvalue(i)
          println(eigVals(i))
          eigVals(i).isReal() should be(true)
        }
        some(eigVals)
      },
      None)

  }
}
