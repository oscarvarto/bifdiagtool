package umich.parser

import scala.collection.immutable.TreeSet

object Names {
  case class VarName(name: String) extends AnyVal
  case class ConstName(name: String) extends AnyVal
  case class ParamName(name: String) extends AnyVal

  implicit object VarNameOrdering extends Ordering[VarName] {
    def compare(a: VarName, b: VarName) = a.name compare b.name
  }

  implicit object ConstNameOrdering extends Ordering[ConstName] {
    def compare(a: ConstName, b: ConstName) = a.name compare b.name
  }

  implicit object ParamNameOrdering extends Ordering[ParamName] {
    def compare(a: ParamName, b: ParamName) = a.name compare b.name
  }

  class NameMap[K, V]
  implicit val varToNum = new NameMap[VarName, Num]
  implicit val constToNum = new NameMap[ConstName, Num]
  implicit val paramToNum = new NameMap[ParamName, Num]

  import shapeless.HMap

  type TableVals = HMap[NameMap]

  def mapAdd(m: Map[ConstName, Num], hmap: TableVals): TableVals = {
    val maybePair = m.headOption
    if (maybePair == None) hmap else mapAdd(m.drop(1), hmap + maybePair.get)
  }

}
