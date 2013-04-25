package umich.gui

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

import scalaz.std.option._
import scalaz.std.option.optionSyntax._
import scalaz.std.string._
import stringSyntax._

class OptionTest extends FunSuite with ShouldMatchers {
  test("An empty name for the project is not Ok") {
    "".charsNel map { _.list.mkString } should be(None)
  }

  test("A non-empty name for the project should be Ok") {
    "HelloWorldProject".charsNel map { _.list.mkString } should be(Some("HelloWorldProject"))
  }
}
