package umich.validation

import scalaz.{
  NonEmptyList,
  Validation,
  ValidationNel
}

import scalaz.syntax.validation._

class Name(val s: String) extends AnyVal
object Name {
  def apply(s: String): Validation[String, Name] = if (s.headOption.exists(_.isUpper))
    new Name(s).success
  else
    "Name must start with a capital letter".fail
}

class Age(val a: Int) extends AnyVal
object Age {
  def apply(a: Int): Validation[String, Age] = if (0 to 130 contains a)
    new Age(a).success
  else
    "Age must be in range".fail
}

case class Person(name: Name, age: Age)
object Person {
  def mkPerson(name: String, age: Int): Validation[NonEmptyList[String], Person] = {
    //val V = Validation.ValidationApplicative[NonEmptyList[String]]
    import scalaz.syntax.apply._

    val vnelName = Name(name).toValidationNel[String, Name]
    val vnelAge = Age(age).toValidationNel[String, Age]

    (vnelName |@| vnelAge) { (n, a) => Person(n, a) }
  }
}

object ValEx2 extends App {
  import Person.mkPerson
  import scalaz.std.option._
  import scalaz.NonEmptyList.nels

  assert(mkPerson("Bob", 31).isSuccess == true, "Failed to create Bob")

  val errorMessages = mkPerson("bob", 131).swap.toOption
  val expectedErrorMessages = some(nels("Name must start with a capital letter", "Age must be in range"))
  assert(errorMessages equals expectedErrorMessages, "Not getting a NEL[String]")
}
