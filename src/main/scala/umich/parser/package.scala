package umich

package object parser {

  private[parser]type Num = Double

  private[parser] object Num {
    def addition(lhs: Num, rhs: Num) = lhs + rhs
    def subtraction(lhs: Num, rhs: Num) = lhs - rhs
    def product(lhs: Num, rhs: Num) = lhs * rhs
    def division(lhs: Num, rhs: Num) = lhs / rhs
    def pow(lhs: Num, rhs: Num) = math.pow(lhs, rhs)
  }

  private[parser] implicit def exprToCanEvaluate(e: Expr) = e.asInstanceOf[CanEvaluate]
}
