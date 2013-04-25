package umich.parser

import scala.collection.immutable.TreeSet
import Names._

trait DynSys {
  def eval(input: TableVals): Array[Double]
  def evalNorms(input: TableVals): Array[Double]
  def namesVariables: TreeSet[VarName]
  def namesParams: TreeSet[ParamName]
  def constants: Map[ConstName, Double]
  def numberVariables: Int
  def numberParams: Int
  def numberNorms: Int
}
