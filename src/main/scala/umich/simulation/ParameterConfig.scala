package umich.simulation

import scalaz.ValidationNel

class ParameterConfig private (
  val name: String,
  val from: Double,
  val to: Double,
  val step: Double)

object ParameterConfig {
  def apply(
    name: String,
    from: Double,
    to: Double,
    step: Double): ValidationNel[String, ParameterConfig] = {
    // Condition 1: from < to
    // val vnelCond1 = if (from < to)
    import scalaz.syntax.validation._
    "from must be less than to".failureNel[ParameterConfig]
  }
}
