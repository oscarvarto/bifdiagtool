package umich.parser

object DynSysSubcriticalPitchforkBif {
  val code =
    """
       |x' = _r*x + x^3 - x^5
       |Norm1 = x
    """.stripMargin

  import math.sqrt
  import shapeless.HMap
  import Names._

  val input = HMap[NameMap](
    VarName("x") → 0.70710678,
    ParamName("_r") → -0.25)
  private val res = Parser.parse(code)

  val dynSys = res.get
}
