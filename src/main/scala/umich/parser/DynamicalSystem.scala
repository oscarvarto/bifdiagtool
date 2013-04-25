package umich.parser

import scala.sys.error
import scalaz._, Scalaz._
import Names._
import scala.collection.immutable.TreeSet

sealed abstract class Expr

sealed trait CanEvaluate {
  def eval: Reader[TableVals, Num]
}

trait CanSearchVarsInNorm {
  def searchVars(): TreeSet[VarName]
}

trait CanSearchParams {
  def searchParams(): TreeSet[ParamName]
}

object ExprOps {
  implicit def exprToCanSearchVarsInNorm(e: Expr) = e.asInstanceOf[CanSearchVarsInNorm]
  implicit def exprToCanSearchParams(e: Expr) = e.asInstanceOf[CanSearchParams]
}

import ExprOps._

case class DynamicalSystem(exprs: List[Expr]) extends Expr with DynSys {
  override def toString = exprs mkString "\n"

  private val ast = exprs.foldLeft(AST()) { (ast, expr) ⇒
    expr match {
      case eq @ Equation(dotStVar, e) ⇒
        val astAux = ast.addEquation(eq).addVarName(VarName(dotStVar.name.drop(1)))
        val paramNames = e.searchParams()
        astAux.addParamNames(paramNames)
      case ConstantDefinition(const, number) ⇒
        ast.addConst((ConstName(const.name), number.value))
      case normDef @ NormDefinition(norm, e) ⇒
        val names = e.searchVars() // Find names variables in e
        ast.addNamesVarsInNorm(names).addNorm(normDef)
      case _ ⇒ ast
    }
  }

  def eval(input: TableVals): Array[Num] = {
    //require(input.underlying.keySet == ast.namesVariables ++ ast.namesParams, "eval didn't receive enough data")
    val completeInput = mapAdd(constants, input)
    val evaluation = for {
      eq ← ast.equations.toArray
    } yield eq.eval(completeInput)
    //{ println(eq); eq.expr.eval(completeInput) }
    evaluation
  }

  def evalNorms(input: TableVals): Array[Num] = {
    val completeInput = mapAdd(constants, input)
    val evaluation = for {
      norm ← ast.norms.toArray
    } yield norm.eval(completeInput)
    evaluation
  }

  val namesVariables: TreeSet[VarName] = ast.namesVariables
  val numberVariables = namesVariables.size

  val constants: Map[ConstName, Num] = ast.constants

  val namesParams: TreeSet[ParamName] = ast.namesParams
  val numberParams = namesParams.size

  val numberNorms = ast.norms.size
}

private[parser] case class Equation(dotStVar: DotStateVar, expr: Expr)
    extends Expr with CanEvaluate {
  def eval = for {
    num ← expr.eval
  } yield num

}

private[parser] case class ConstantDefinition(c: Constant, v: Number) extends Expr

//private[parser] case class ParamDeclaration(param: Parameter) extends Expr

private[parser] case class NormDefinition(norm: String, expr: Expr)
    extends Expr with CanEvaluate {
  def eval = for {
    num ← expr.eval
  } yield num
}

private[parser] sealed abstract class BinaryOp(lhs: Expr, rhs: Expr)(f: (Num, Num) ⇒ Num)
    extends Expr with CanEvaluate with CanSearchParams with CanSearchVarsInNorm {
  def eval = for {
    numlhs ← lhs.eval
    numrhs ← rhs.eval
  } yield f(numlhs, numrhs)

  def searchParams() = lhs.searchParams() ++ rhs.searchParams()
  def searchVars() = lhs.searchVars() ++ rhs.searchVars()
}

private[parser] case class Add(lhs: Expr, rhs: Expr) extends BinaryOp(lhs, rhs)(Num.addition)
private[parser] case class Sub(lhs: Expr, rhs: Expr) extends BinaryOp(lhs, rhs)(Num.subtraction)
private[parser] case class Mul(lhs: Expr, rhs: Expr) extends BinaryOp(lhs, rhs)(Num.product)
private[parser] case class Div(lhs: Expr, rhs: Expr) extends BinaryOp(lhs, rhs)(Num.division)
private[parser] case class Pow(lhs: Expr, rhs: Expr) extends BinaryOp(lhs, rhs)(Num.pow)

private[parser] case class Neg(expr: Expr) extends Expr with CanEvaluate with CanSearchParams with CanSearchVarsInNorm {
  def eval = for {
    num ← expr.eval
  } yield (-num)

  def searchParams() = expr.searchParams()
  def searchVars() = expr.searchVars()
}

/** Function application. */
private[parser] case class App(f: String, arg: Expr) extends Expr with CanEvaluate with CanSearchParams with CanSearchVarsInNorm {
  def eval = {
    val fReader = for {
      argNum ← arg.eval
    } yield argNum

    (f, fReader) match {
      case ("Sin", _) ⇒ Reader { (fReader >=> Reader { math.sin _ }) apply (_: TableVals) }
      case ("Cos", _) ⇒ Reader { (fReader >=> Reader { math.cos _ }) apply (_: TableVals) }
      // ...
      case (f, x) ⇒ error(f + " cannot be applied as a function to the argument " + x)
    }
  }

  def searchParams() = arg.searchParams()
  def searchVars() = arg.searchVars()
}

private[parser] case class Number(value: Double) extends Expr with CanEvaluate with CanSearchParams with CanSearchVarsInNorm {
  def eval = Reader { input ⇒ value }

  def searchParams() = TreeSet.empty
  def searchVars() = TreeSet.empty
}

private[parser] case class DotStateVar(name: String) extends Expr

private[parser] case class StateVar(name: String) extends Expr with CanEvaluate with CanSearchParams with CanSearchVarsInNorm {
  def eval = Reader { input ⇒
    input.get(VarName(name)).get
  }
  def searchParams() = TreeSet.empty
  def searchVars() = TreeSet(VarName(name))
}

private[parser] case class Constant(name: String) extends Expr with CanEvaluate with CanSearchParams with CanSearchVarsInNorm {
  def eval = Reader { input ⇒
    input.get(ConstName(name)).get
  }
  def searchParams() = TreeSet.empty //[ParamName]
  def searchVars() = TreeSet.empty //[VarName]
}

private[parser] case class Parameter(name: String) extends Expr with CanEvaluate with CanSearchParams with CanSearchVarsInNorm {
  def eval = Reader { input ⇒
    input.get(ParamName(name)).get
  }

  def searchParams() = TreeSet(ParamName(name))
  def searchVars() = TreeSet.empty
}

