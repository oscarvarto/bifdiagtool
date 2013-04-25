package umich.parser

import scala.collection.immutable.TreeSet
import Names._
import shapeless._
import shapeless.Nat._

private[parser] object auxOrdering {
  implicit object EquationOrdering extends Ordering[Equation] {
    def compare(a: Equation, b: Equation) = a.dotStVar.name compare b.dotStVar.name
  }

  implicit object NormDefinitionOrdering extends Ordering[NormDefinition] {
    def compare(a: NormDefinition, b: NormDefinition) = a.norm compare b.norm
  }
}

import auxOrdering._

private[parser] case class AST(
    equations: TreeSet[Equation] = TreeSet.empty,
    namesVariables: TreeSet[VarName] = TreeSet.empty,
    constants: Map[ConstName, Num] = Map.empty,
    namesParams: TreeSet[ParamName] = TreeSet.empty,
    norms: TreeSet[NormDefinition] = TreeSet.empty,
    namesVariablesInNorm: TreeSet[VarName] = TreeSet.empty) {

  import AST._

  def addEquation(eq: Equation) = {
    val equations = equationsLens.get(this) + eq
    equationsLens.set(this)(equations)
  }

  def addVarName(varName: VarName) = {
    val names = namesVarLens.get(this) + varName
    namesVarLens.set(this)(names)
  }

  def addConst(kv: (ConstName, Num)) = {
    val constants = constLens.get(this) + kv
    constLens.set(this)(constants)
  }

  def addParamName(paramName: ParamName) = {
    val names = namesParLens.get(this) + paramName
    namesParLens.set(this)(names)
  }

  def addParamNames(n: TreeSet[ParamName]) = {
    def add(namesToAdd: TreeSet[ParamName], acc: AST): AST = {
      val maybeName = namesToAdd.headOption
      if (maybeName == None) acc else add(namesToAdd.drop(1), acc.addParamName(maybeName.get))
    }
    add(n, this)
  }

  def addNorm(n: NormDefinition) = {
    val norms = normsLens.get(this) + n
    normsLens.set(this)(norms)
  }

  def addNamesVarsInNorm(varNames: TreeSet[VarName]) = {
    val names = namesVarNormLens.get(this) ++ varNames
    namesVarNormLens.set(this)(names)
  }
}

private[parser] object AST {
  implicit val astIso = Iso.hlist(AST.apply _, AST.unapply _)
  val equationsLens = Lens[AST] >> _0
  val namesVarLens = Lens[AST] >> _1
  val constLens = Lens[AST] >> _2
  val namesParLens = Lens[AST] >> _3
  val normsLens = Lens[AST] >> _4
  val namesVarNormLens = Lens[AST] >> _5
}
