import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtScalariform._
import Resolver.sonatypeRepo

object Build extends Build {

  lazy val guitesting = Project(
    "bidiatool",
    file("."),
    settings = commonSettings ++ Seq(
      libraryDependencies ++= Seq(
      )
    )
  ).configs( nonGUITests )
  .settings( inConfig(nonGUITests)(Defaults.testSettings): _* )
  .settings(
    testOptions in nonGUITests := Seq(
      Tests.Argument("-l", "umich.guitesting.tags.GUITest")
    )
  )

  lazy val nonGUITests = config("nongui") extend(Test)

  def commonSettings =
    Defaults.defaultSettings ++ 
    scalariformSettings ++
    Seq(
      organization := "umich",
      // version is defined in version.sbt to support sbt-release
      scalaVersion := Version.scala,
      scalacOptions ++= Seq(
        "-unchecked",
        "-deprecation",
        "-Xlint",
        "-language:_", 
        "-target:jvm-1.7",
        "-encoding", "UTF-8"
      ),
      libraryDependencies ++= Seq(
        Dependency.Compile.scalazCore,
        Dependency.Compile.functionalJava,
        Dependency.Compile.shapeless,
        Dependency.Compile.spireMath,
        Dependency.Test.junitInterface,
        Dependency.Test.festSwing,
        Dependency.Test.festSwingJUnit,
        Dependency.Test.scalaMockScalaTest
      ),
      resolvers ++= Seq(
        sonatypeRepo("snapshots"),
        sonatypeRepo("releases")
      ),
      parallelExecution in Test := false
    )

  object Version {
    val scala = "2.10.1"
  }

  object Dependency {

    object Compile {
      val scalazCore = "org.scalaz" % "scalaz-core_2.10" % "7.0.0" withSources() withJavadoc()
      val shapeless = "com.chuusai" % "shapeless_2.10" % "1.2.4" withSources() withJavadoc()
      val functionalJava = "org.functionaljava" % "functionaljava" % "3.1" withSources() withJavadoc()
      val spireMath = "org.spire-math" % "spire_2.10" % "0.4.0-M4" withSources() withJavadoc()
    }

    object Test {
      val junitInterface = "com.novocode" % "junit-interface" % "0.10-M3" % "test"
      val festSwing = "org.easytesting" % "fest-swing" % "1.2.1" % "test" withSources() 
      val festSwingJUnit = "org.easytesting" % "fest-swing-junit" % "1.2.1" % "test" withSources() 
      val scalaMockScalaTest = "org.scalamock" %% "scalamock-scalatest-support" % "3.0.1" % "test" withSources() withJavadoc()
    }
  }
}
