package umich.simulation

import shapeless._
import umich.parser.Names._

case class OneVariableFunctionEvaluator(proj: Project) {
  def f(closedParams: Map[ParamName, Double]): Double ⇒ Double = { x ⇒
    val varNameSet: Set[VarName] = proj.dynSys.namesVariables diff proj.fixedValues.head.keySet
    require(varNameSet.size == 1, "Not a One Variable Function problem")
    val fVarName = varNameSet.head
    val fVarMap = Map(fVarName → x)
    val dataHlist = fVarMap :: closedParams :: proj.fixedValues
    import umich.bidiatool.polyFuncs.appendMap
    val data: HMap[NameMap] = dataHlist.foldLeft(HMap.empty[NameMap])(appendMap)
    proj.dynSys.eval(data)(0)
  }
}
