package umich.parser

object DynSysSpring {
  val code =
    """
       |x' = v
       |v' = -_w0^2*x - 2*_c*v
       |Norm1 = x
    """.stripMargin

  import shapeless.HMap
  import Names._

  // (x, v) = (0, 0) is the only fixed point of this system
  // Next input (_c > _w0) corresponds to a overdamped system
  val input = HMap[NameMap](
    VarName("x") → 0.0,
    VarName("v") → 0.0,
    ParamName("_w0") → 2.0,
    ParamName("_c") → 4.0)
  private val res = Parser.parse(code)

  val dynSys = res.get
}
