package umich.parser

import scala.collection.immutable.TreeSet

object Names {
  trait Name extends Any {
    def name: String
  }
  case class VarName(name: String) extends AnyVal with Name
  case class ConstName(name: String) extends AnyVal with Name
  case class ParamName(name: String) extends AnyVal with Name

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
  implicit val varToNum = new NameMap[VarName, Double]
  implicit val constToNum = new NameMap[ConstName, Double]
  implicit val paramToNum = new NameMap[ParamName, Double]

  import shapeless.HMap

  type TableVals = HMap[NameMap]

  def mapAdd(m: Map[ConstName, Double], hmap: TableVals): TableVals = {
    val maybePair = m.headOption
    if (maybePair == None) hmap else mapAdd(m.drop(1), hmap + maybePair.get)
  }

}
