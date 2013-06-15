package umich.stability

import org.scalatest.{ FunSuite, Matchers }
import spire.algebra._ // provides algebraic type classes
import spire.math._ // provides functions, types, and type classes
// import spire.implicits._ // provides infix operators, instances and conversions

class NumericalDifferentiationTest extends FunSuite with Matchers {
  //import shapeless._

  test("One variable functions, with Doubles") {
    import spire.std.double._
    import spire.ops._
    import NumDiff.rid
    info("-(x pow 2). See Strogatz 3.1, eq(2)")
    def g1[A: Fractional](x: A): A = -x.pow(2)
    g1(2.0) should be(-4.0)
    val f1 = rid(0.5)(g1(_))
    f1(2.0) should be(-4.0 +- 1e-5)

    info("1 - x - exp(-x). See Strogatz, example 3.1.2")
    def g2[A: Fractional: Trig](x: A): A = 1 - x - exp(-x)
    g2(2.0) should be(-1.1353 +- 1e-4)
    val f2 = rid(0.5)(g2(_))
    f2(2.0) should be(-0.86466 +- 1e-5)

    info("-log(x) - x + 1. See Strogatz, example 3.2.2")
    def g3[A: Fractional: Trig](x: A): A = -log(x) - x + 1
    g3(e) should be(-e +- 1e-5)
    val f3 = rid(1.0)(g3(_))
    f3(e) should be(-1.36788 +- 1e-5)

    info("-x + tanh(x). See Strogatz, example 3.4.1")
    def g4[A: Fractional: Trig](x: A): A = -x + tanh(x)
    g4(0.0) should be(0.0 +- 1e-4)
    val f4 = rid(0.5)(g4(_))
    f4(0.0) should be(0.0 +- 1e-5)

    info("sin(φ)[1.2 cos(φ) - 1]. See Strogatz 3.5, eq (2), page 62")
    val γ = 1.2
    def g5[A: Fractional: Trig](φ: A): A = sin(φ) * (γ * cos(φ) - 1)
    val φFixedPoint = acos(1.0 / γ)
    g5(φFixedPoint) should be(0.0 +- 1e-4)
    val f5 = rid(0.5)(g5(_))
    f5(φFixedPoint) should be(-0.36666 +- 1e-5)

    info("Insect outbreak. See Strogatz 3.7, eq (3), page 75")
    val (r, k) = (0.4, 30.0)
    def g6[A: Fractional](x: A): A = r * x * (1 - x / k) - x.pow(2) / (1 + x.pow(2))
    g6(0.486738) should be(0.0 +- 1e-4)
    val f6 = rid(0.5)(g6(_))
    f6(0.486738) should be(-0.24926 +- 1e-5)
  }

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
}
