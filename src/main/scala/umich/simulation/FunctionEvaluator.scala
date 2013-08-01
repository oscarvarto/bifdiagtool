package umich.simulation

import shapeless._
import umich.parser.Names._
import umich.bidiatool.polyFuncs.appendMap

case class OneVariableFunctionEvaluator(proj: Project) {
  def f(closedParams: Map[ParamName, Double]): Double ⇒ Double = { x ⇒
    val varNameSet: Set[VarName] = proj.dynSys.namesVariables diff proj.fixedValues.head.keySet
    require(varNameSet.size == 1, "Not a One Variable Function problem")
    val fVarName = varNameSet.head
    val fVarMap = Map(fVarName → x)
    val dataHlist = fVarMap :: closedParams :: proj.fixedValues
    val data: HMap[NameMap] = dataHlist.foldLeft(HMap.empty[NameMap])(appendMap)
    proj.dynSys.eval(data)(0)
  }
}

case class SeveralVariableFunctionEvaluator(proj: Project) {
  import collection.immutable.TreeSet
  def f(closedParams: Map[ParamName, Double]): Array[Double] ⇒ Array[Double] = { X ⇒
    val varNameSet: TreeSet[VarName] = proj.dynSys.namesVariables diff proj.fixedValues.head.keySet
    require(varNameSet.size > 1, "Not a Several Variables Function Problem")
    val fVarMap: Map[VarName, Double] = varNameSet.toSeq.zip(X).toMap
    val dataHlist = fVarMap :: closedParams :: proj.fixedValues
    val data: HMap[NameMap] = dataHlist.foldLeft(HMap.empty[NameMap])(appendMap)
    proj.dynSys.eval(data)
  }
}
