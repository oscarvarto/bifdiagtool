package umich.stability

import org.scalatest.{ FunSuite, GivenWhenThen, Matchers }
import spire.algebra._ // provides algebraic type classes
import spire.math._ // provides functions, types, and type classes

class JacobianTest extends FunSuite with Matchers with GivenWhenThen {
  import org.ejml.data.DenseMatrix64F
  import org.ejml.ops.NormOps.normF
  import org.ejml.ops.CommonOps.sub

  test("f1[{x_, y_}] := {x^2 y, 5 x + Sin[y]}, with Doubles") {
    import NumDiff.jacobian

    val η = 1.0e-10
    val Sx = Array(1.0, 1.0)
    val tol = 1.0e-4

    val fvec1: Array[Double] ⇒ Array[Double] = { X ⇒
      val x = X(0)
      val y = X(1)
      Array(x * x * y, 5.0 * x + math.sin(y))
    }

    // To print jac1: jac1.map { a ⇒ println(a.mkString(",")) }
    val xc1 = Array(1.0, 2.0)
    val jac1 = new DenseMatrix64F(jacobian(η)(Sx)(fvec1, xc1))
    val trueJac1 = new DenseMatrix64F(Array(
      Array(4.0, 1.0),
      Array(5.0, -0.416147)))
    val errorMatrix1 = new DenseMatrix64F(2, 2)
    sub(trueJac1, jac1, errorMatrix1)
    normF(errorMatrix1) should be <= (tol)

    val xc2 = Array(20.0, 0.1)
    val jac2 = new DenseMatrix64F(jacobian(η)(Sx)(fvec1, xc2))
    val trueJac2 = new DenseMatrix64F(Array(
      Array(4.0, 400.0),
      Array(5.0, 0.995004)))
    val errorMatrix2 = new DenseMatrix64F(2, 2)
    sub(trueJac2, jac2, errorMatrix2)
    normF(errorMatrix2) should be <= (tol)
  }

  test("f2[{r_, θ_, φ_ }] = {r Sin[θ] Cos[φ], r Sin[θ] Sin[φ], r Cos[θ]}, with Doubles") {
    import math.{ sin, cos, Pi }
    import NumDiff.jacobian

    val η = 1.0e-10
    val Sx = Array(1.0, 1.0, 1.0)
    val tol = 1.0e-4

    val fvec2: Array[Double] ⇒ Array[Double] = X ⇒
      X match {
        case Array(r, θ, φ) ⇒ Array(r * sin(θ) * cos(φ), r * sin(θ) * sin(φ), r * cos(θ))
      }

    val xc3 = Array(10.0, Pi / 2.0, Pi / 4.0)
    val jac3 = new DenseMatrix64F(jacobian(η)(Sx)(fvec2, xc3))
    val trueJac3 = new DenseMatrix64F(Array(
      Array(0.707107, 0.0, -7.07107),
      Array(0.707107, 0.0, 7.07107),
      Array(0.0, -10.0, 0.0)))
    val errorMatrix3 = new DenseMatrix64F(3, 3)
    sub(trueJac3, jac3, errorMatrix3)
    normF(errorMatrix3) should be <= (tol)

    val xc4 = Array(1.0, Pi / 8.0, Pi / 16.0)
    val jac4 = new DenseMatrix64F(jacobian(η)(Sx)(fvec2, xc4))
    val trueJac4 = new DenseMatrix64F(Array(
      Array(0.37533, 0.906127, -0.0746578),
      Array(0.0746578, 0.18024, 0.37533),
      Array(0.92388, -0.382683, 0.0)))

    val errorMatrix4 = new DenseMatrix64F(3, 3)
    sub(trueJac4, jac4, errorMatrix4)
    normF(errorMatrix4) should be <= (tol)
  }

  test("A damped, undriven Hooke's Law Spring") {
    Given("The Function Evaluator for the dampedUndrivenHookeLawSpringProject")
    import umich.simulation.SeveralVariableFunctionEvaluator
    import umich.simulation.dampedUndrivenHookeLawSpringProject
    val fEv = SeveralVariableFunctionEvaluator(dampedUndrivenHookeLawSpringProject)
    Given("closedParams for a overdamped system _c > _w0")
    import umich.parser.Names._
    val closedParams1 = Map(ParamName("_c") → 4.0, ParamName("_w0") → 2.0)
    val f1 = fEv.f(closedParams1)
    val xc = Array(0.0, 0.0)
    val η = 1.0e-10
    val Sx = Array(1.0, 1.0)
    val tol = 1.0e-5
    import NumDiff.jacobian
    val jac1Data = jacobian(η)(Sx)(f1, xc)
    val jac1 = new DenseMatrix64F(jac1Data)
    def computeTrueJac(c: Double, w0: Double) = new DenseMatrix64F(Array(
      Array(-2.0 * c, -w0 * w0),
      Array(1.0, 0.0)))
    val errorMatrix1 = new DenseMatrix64F(2, 2)
    sub(computeTrueJac(4.0, 2.0), jac1, errorMatrix1)
    Then("The jacobian for X = [0, 0]^T, (_c, _w0) = (4.0, 2.0) is computed correctly")
    normF(errorMatrix1) should be <= (tol)
    And("The fixed point is classified correctly as a Sink")
    import stabilityClassifier.classify
    classify(jac1Data) should be(Some(Right(Sink)))

    Given("closedParams for a critically damped system _c = _w0")
    val closedParams2 = Map(ParamName("_c") → 2.0, ParamName("_w0") → 2.0)
    val f2 = fEv.f(closedParams2)
    val jac2Data = jacobian(η)(Sx)(f2, xc)
    val jac2 = new DenseMatrix64F(jac2Data)
    val errorMatrix2 = new DenseMatrix64F(2, 2)
    sub(computeTrueJac(2.0, 2.0), jac2, errorMatrix2)
    Then("The jacobian for X = [0, 0]^T, (_c, _w0) = (2.0, 2.0) is computed correctly")
    normF(errorMatrix2) should be <= (tol)
    And("The fixed point is classified incorrectly as a Spiral Sink")
    val trueJac2Data = Array(
      Array(-4.0, -4.0),
      Array(1.0, 0.0))
    import org.ejml.alg.dense.decomposition.eig.EigenvalueSmall
    val m = new DenseMatrix64F(trueJac2Data)
    val n = m.numRows
    val eigDec = new EigenvalueSmall()
    eigDec.value2x2(-4.0, -4.0, 1.0, 0.0)
    println(eigDec.value0)
    println(eigDec.value1)
    // import scalaz.std.option._
    // import scalaz.syntax.std.boolean._
    // import spire.syntax._
    // import org.ejml.data.Complex64F
    // val maybeEigVals = eigDec.decompose(m).fold(
    //   {
    //     val eigVals = Array.ofDim[Complex64F](n)
    //     cfor(0)(_ < n, _ + 1) { i ⇒
    //       eigVals(i) = eigDec.getEigenvalue(i)
    //       println(eigVals(i))
    //     }
    //     some(eigVals)
    //   },
    //   None)

    // classify(jac2Data) should be(Some(Left(Undefined)))

    Given("closedParams for a underdamped system _c < _w0")
    val closedParams3 = Map(ParamName("_c") → 2.0, ParamName("_w0") → 4.0)
    val f3 = fEv.f(closedParams3)
    val jac3Data = jacobian(η)(Sx)(f3, xc)
    val jac3 = new DenseMatrix64F(jac3Data)
    val errorMatrix3 = new DenseMatrix64F(2, 2)
    sub(computeTrueJac(2.0, 4.0), jac3, errorMatrix3)
    Then("The jacobian for X = [0, 0]^T, (_c, _w0) = (2.0, 4.0) is computed correctly")
    normF(errorMatrix3) should be <= (tol)
    And("The fixed point is classified correctly as a Spiral Sink")
    classify(jac3Data) should be(Some(Right(SpiralSink)))
  }
}
