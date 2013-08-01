package umich.stability

import org.scalatest.{ FunSuite, Matchers }

// This tests take as input a *given* Jacobian matrix
// (invented by Oscar Vargas Torres), and use the eigenvalues of these matrices
// to classify the "fixed point"

class StabilityClassificationTest1 extends FunSuite with Matchers {
  import stabilityClassifier.classify

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
