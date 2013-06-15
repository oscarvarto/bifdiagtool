package umich.parser

object DynSysSaddleNode {
  val code =
    """
       |x' = _r + x^2
       |Norm1 = x
    """.stripMargin

  import math.sqrt
  import shapeless.HMap
  import Names._

  val input = HMap[NameMap](
    VarName("x") → 2.0,
    ParamName("_r") → -4.0)
  private val res = Parser.parse(code)

  val dynSys = res.get
}
