package umich.parser

object DynSys2 {
  val code =
    """
      |x' = x^2 + _a + _b
      |Norm1 = x
    """.stripMargin

  import shapeless.HMap
  import Names._

  val input = HMap[NameMap](
    VarName("x") → 0.0,
    ParamName("_a") → 0.0,
    ParamName("_b") → 0.0)

  val dynSys = Parser.parse(code).get
}
