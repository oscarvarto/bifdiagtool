package umich.simulation

import scalaz.{
  Validation,
  ValidationNel
}

import scalaz.syntax.validation._

class ParameterConfig private (
  val from: Double,
  val to: Double,
  val step: Double)

object ParameterConfig {
  def apply(
    from: Double,
    to: Double,
    step: Double): ValidationNel[String, ParameterConfig] = {
    val vnelRange = Range(from, to, cond1Msg)
      .toValidationNel[String, Range]
    val vnelStep = Range(
      step,
      (to - from).abs,
      cond2Msg).toValidationNel[String, Range]
    import scalaz.syntax.apply._
    (vnelRange ⊛ vnelStep) { (r, s) ⇒
      new ParameterConfig(r.from, r.to, s.from)
    }
  }

  val cond1Msg = "From must be less or equal than To"
  val cond2Msg = "Step must be less or equal than given range"
}

class Range private (val from: Double, val to: Double)
object Range {
  // checks if first argument is less than second argument
  def apply(from: Double, to: Double, errorMsg: String): Validation[String, Range] = {
    if (from <= to)
      new Range(from, to).success
    else
      errorMsg.fail
  }
}
