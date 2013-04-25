package umich.parser

object DynSys1 {
  val code =
    """
       |x1' = x2
       |x2' = -_w^2*x1 - 2*MU*x2 + F*Cos[OMEGA]
       |F = 10
       |MU = 2
       |OMEGA = 2
       |Norm1 = x1^2 + x2^2
    """.stripMargin

  import math.cos
  import shapeless.HMap
  import Names._

  val x1 = 10.0 * cos(2) / 9.0

  val input = HMap[NameMap](
    VarName("x1") → x1,
    VarName("x2") → 0.0,
    ParamName("_w") → 3.0)
  private val res = Parser.parse(code)

  val dynSys = res.get
}
