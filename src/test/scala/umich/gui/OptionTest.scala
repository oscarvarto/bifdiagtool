package umich.gui

import org.scalatest.FunSuite
import org.scalatest.Matchers

import scalaz.std.option._
import scalaz.std.option.optionSyntax._
import scalaz.std.string._
import stringSyntax._

class OptionTest extends FunSuite with Matchers {
  test("An empty name for the project is not Ok") {
    "".charsNel map { _.list.mkString } should be(None)
  }

  test("A non-empty name for the project should be Ok") {
    "HelloWorldProject".charsNel map { _.list.mkString } should be(Some("HelloWorldProject"))
  }

  test("Option as a monoid ") {
    import umich.parser.Names._
    import scalaz.{ @@, Tags }
    import scalaz.syntax.monoid._
    type λ = Option[Name] @@ Tags.First
    val x1: λ = Tags.First(VarName("x").some)
    val x2: λ = Tags.First(none: Option[ParamName])
    val y = x1 |+| x2
    println(y)
  }
}
