package umich.stability

import spire.algebra._
import spire.math._
import spire.implicits._
import scala.reflect.ClassTag

object NumDiff {
  def rid[A: Fractional: ClassTag](h: A)(fn: A ⇒ A): A ⇒ A = { x ⇒
    import shapeless._
    import Record._

    object guess extends Field[A]
    object col extends Field[Array[A]]
    object error extends Field[A]
    object factor extends Field[A]

    type λ = (guess.type, A) :: (col.type, Array[A]) :: (error.type, A) :: (factor.type, A) :: HNil

    def estimate(h0: A): A = (fn(x + h0) - fn(x - h0)) / (2.0 * h0)

    val con: A = Fractional[A].fromDouble(1.4)
    def derivative(prev: λ, hh: A): A = {
      val nextRecord: λ = {
        val initRec: λ = (guess → prev.get(guess)) ::
          (col → Array(estimate(hh))) ::
          (error → prev.get(error)) ::
          (factor → (con * con)) :: HNil
        prev.get(col).foldLeft(initRec) { (rec, n) ⇒
          val fac = rec.get(factor)
          val nextGuess = (rec.get(col).last * fac - n) / (fac - 1.0)
          val prevGuess = rec.get(guess)
          val nextError: A = Fractional[A].max(
            Fractional[A].abs(nextGuess - rec.get(col).last),
            Fractional[A].abs(nextGuess - n))
          val prevError = rec.get(error)
          if (nextError <= prevError) {
            (guess → nextGuess) ::
              (col → (rec.get(col) :+ nextGuess)) ::
              (error → nextError) ::
              (factor → (fac * con)) ::
              HNil
          } else {
            (rec + (col → (rec.get(col) :+ nextGuess))) + (factor → (fac * con))
          }
        }
      }
      val higherOrderWorse = {
        val safe: A = Fractional[A].fromDouble(2.0)
        Fractional[A].abs(nextRecord.get(col).last - prev.get(col).last) >=
          safe * nextRecord.get(error)
      }

      if (higherOrderWorse) nextRecord.get(guess) else derivative(nextRecord, hh / con)
    }

    val firstGuess = estimate(h)
    val firstRecord: λ = (guess → firstGuess) ::
      (col → Array(firstGuess)) ::
      (error → Fractional[A].fromDouble(Double.MaxValue)) ::
      (factor → con) ::
      HNil

    derivative(firstRecord, h / con)
  }

  // Fractional[A].fromDouble(1e-7)
  def jacobian[A: Fractional: ClassTag](η: A)(Sx: Array[A])(fvec: Array[A] ⇒ Array[A], xc: Array[A]): Array[Array[A]] = {
    val Fc = fvec(xc)
    require(Fc.size == xc.size, "fvec must map an array of size n to an array of the same size")
    val sqrtη = Fractional[A].sqrt(η)
    val n = xc.size
    val Jac = Array.fill(n, n)(Fractional[A].zero)
    import spire.syntax._
    cfor(0)(_ < n, _ + 1) { j ⇒
      val maxj = Fractional[A].max(
        Fractional[A].abs(xc(j)),
        Fractional[A].reciprocal(Sx(j)))
      val sign = if (Fractional[A].isNegative(xc(j))) {
        -Fractional[A].one
      } else {
        Fractional[A].one
      }
      val stepj = sqrtη * maxj * sign
      val tempj = xc(j)
      xc(j) = xc(j) + stepj
      val stepsizej = xc(j) - tempj
      val Fj = fvec(xc)
      cfor(0)(_ < n, _ + 1) { i ⇒
        Jac(i)(j) = (Fj(i) - Fc(i)) / stepsizej
      }
      xc(j) = tempj
    }
    Jac
  }
}
