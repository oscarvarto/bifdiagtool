package umich.stability

import org.scalatest.{ FunSuite, Matchers }
import spire.algebra._ // provides algebraic type classes
import spire.math._ // provides functions, types, and type classes
import spire.implicits._

class NumericalDifferentiationTest extends FunSuite with Matchers {
  test("One variable functions, with generic functions, used with Doubles") {
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
    g3(Trig[Double].e) should be(-Trig[Double].e +- 1e-5)
    val f3 = rid(1.0)(g3(_))
    f3(Trig[Double].e) should be(-1.36788 +- 1e-5)

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
}
