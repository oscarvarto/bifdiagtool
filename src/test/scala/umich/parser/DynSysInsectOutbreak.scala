package umich.parser

object DynSysInsectOutbreak {
  val code =
    """
       |x' = _r*x*(1 - x/_k) - x^2/(1 + x^2)
       |Norm1 = x
    """.stripMargin

  import math.sqrt
  import shapeless.HMap
  import Names._

  val input = HMap[NameMap](
    VarName("x") → 0.486738282717022,
    ParamName("_r") → 0.4,
    ParamName("_k") → 30.0)
  private val res = Parser.parse(code)

  val dynSys = res.get
}
