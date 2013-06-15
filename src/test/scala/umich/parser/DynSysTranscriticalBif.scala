package umich.parser

object DynSysTranscriticalBif {
  val code =
    """
       |x' = _r*x - x^2
       |Norm1 = x
    """.stripMargin

  import shapeless.HMap
  import Names._

  val input = HMap[NameMap](
    VarName("x") → 1.0,
    ParamName("_r") → 1.0)
  private val res = Parser.parse(code)

  val dynSys = res.get
}
