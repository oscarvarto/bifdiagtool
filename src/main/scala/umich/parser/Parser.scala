package umich.parser

import scala.util.parsing.combinator.{ ImplicitConversions, JavaTokenParsers }

object Parser extends JavaTokenParsers with ImplicitConversions {
  def parse(s: String) = parseAll(system, s)

  def system = rep(sentence) ^^ DynamicalSystem
  def sentence = equation | constantDefinition | normDefinition
  def equation = dotStateVar ~ ("=" ~> expr) ^^ Equation
  //def paramDeclaration = "param" ~> parameter ^^ ParamDeclaration //~ rep("," ~> parameter ^^ ParamDeclaration)
  def constantDefinition = constant ~ ("=" ~> floatingPointNumber) ^^ {
    case c ~ n ⇒ ConstantDefinition(c, Number(n.toDouble))
  }
  def normDefinition = ("Norm1" | "Norm2") ~ ("=" ~> expr) ^^ NormDefinition
  //def variable: Parser[Expr] = dotStateVar | stateVar
  def dotStateVar = """[a-z]\w*""".r <~ "'" ^^ { case id ⇒ DotStateVar("d" + id) }
  def stateVar = """[a-z]\w*""".r <~ not("'") ^^ StateVar
  def constant = """[A-Z][A-Z0-9_]*""".r <~ not("'") ^^ Constant
  def parameter = """_\w+""".r <~ not("'") ^^ Parameter
  def expr: Parser[Expr] = chainl1(prod, "+" ^^^ Add | "-" ^^^ Sub)
  def prod = chainl1(signExp, "*" ^^^ Mul | "/" ^^^ Div)
  def signExp = opt("-") ~ power ^^ { case Some(_) ~ e ⇒ Neg(e); case _ ~ e ⇒ e }
  def power = chainr1(
    appExpr,
    "^" ^^^ Pow,
    Pow,
    Number(1.0)) // chainr1 for right associativity
  def appExpr = (
    ("Cos" | "Sin")
    ~ ("[" ~> expr <~ "]") ^^ { case f ~ arg ⇒ App(f, arg) }
    | simpleExpr)

  //def fun: Parser[Expr] = literal("Cos") | literal("Sin")

  def simpleExpr = (stateVar
    | constant
    | parameter
    | floatingPointNumber ^^ { s ⇒ Number(s.toDouble) } // throws NumberFormatException
    | "(" ~> expr <~ ")")

}
