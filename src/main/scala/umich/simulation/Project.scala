package umich.simulation

import scala.collection.immutable.TreeMap

import shapeless._
import shapeless.Nat._

import umich.parser._
import umich.parser.Names._

import umich.bidiatool.hlistTypes._

case class Project(
  name: String,
  dynSys: DynSys,
  paramConfs: TreeMap[ParamName, ParameterConfig] = TreeMap.empty,
  fixedValues: MVDPD = mvdpdEmpty) {

  def addParamConf(kv: (ParamName, ParameterConfig)): Project = {
    import Project._
    val paramConfs = paramConfsLens.get(this) + kv
    paramConfsLens.set(this)(paramConfs)
  }

  def addFixedValue[T <: Name](kv: (T, Double)): Project = {
    import Project._
    import umich.bidiatool.polyFuncs.addKV
    val newFixed = addKV(fixedValuesLens.get(this))(kv._1, kv._2)
    fixedValuesLens.set(this)(newFixed)
  }

  def names2VarOrPar: MVMP = {
    import umich.bidiatool.polyFuncs._
    val alreadyChosen: LVLP = fixedValues map keysVP
    val namesVariables = dynSys.namesVariables
    val namesVariablesInNorm = dynSys.namesVariablesInNorms
    val possibleFixedVariables = (namesVariables diff namesVariablesInNorm)
      .toList
    val namesParams = dynSys.namesParams
    val paramsChanging = paramConfs.keySet
    val fixedParameters = (namesParams diff paramsChanging).toList
    import umich.gui.ChooseConstantsFrame._
    val candidatesFixed = possibleFixedVariables :: fixedParameters :: HNil
    val candidates = (candidatesFixed zip alreadyChosen) map difference
    val hlistNames = candidates map names
    val pairs = hlistNames zip candidates
    val listOfTuples = pairs map deepZip
    listOfTuples map list2Map
  }

  def startSimulation() {

  }
}

object Project {
  implicit val projectIso = Iso.hlist(Project.apply _, Project.unapply _)
  val paramConfsLens = Lens[Project] >> _2
  val fixedValuesLens = Lens[Project] >> _3
}
