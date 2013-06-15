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

  lazy val formatSettings = scalariformSettings ++ Seq(
    ScalariformKeys.preferences in Compile := formattingPreferences,
    ScalariformKeys.preferences in Test    := formattingPreferences
  )

  def formattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
      .setPreference(RewriteArrowSymbols, true)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
      .setPreference(CompactControlReadability, false)
  }

  def commonSettings =
    Defaults.defaultSettings ++
    formatSettings ++
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
        "-encoding", "UTF-8",
        "-P:continuations:enable"
      ),
      libraryDependencies ++= Seq(
        Dependency.Compile.scalazCore,
        Dependency.Compile.shapeless,
        Dependency.Compile.spireMath,
        Dependency.Compile.ejml,
        Dependency.Compile.scalachart,
        Dependency.Test.scalatest,
        Dependency.Test.festSwing,
        compilerPlugin("org.scala-lang.plugins" % "continuations" % Version.scala)
      ),
      resolvers ++= Seq(
        sonatypeRepo("snapshots"),
        sonatypeRepo("releases")
      ),
      parallelExecution in Test := false,
      fork in run := true
    )

  object Version {
    val scala = "2.10.2"
  }

  object Dependency {

    object Compile {
      val scalazCore = "org.scalaz" % "scalaz-core_2.10" % "7.0.0"
      val shapeless = "com.chuusai" % "shapeless_2.10" % "1.2.4"
      val spireMath = "org.spire-math" % "spire_2.10" % "0.4.0"
      val ejml = "com.googlecode.efficient-java-matrix-library" % "ejml" % "0.22"
      val scalachart = "com.github.wookietreiber" % "scala-chart_2.10" % "0.2.2"
  }

    object Test {
      val scalatest = "org.scalatest" % "scalatest_2.10" % "2.0.M6-SNAP21"
      val festSwing = "org.easytesting" % "fest-swing" % "1.2.1" % "test"
    }
  }
}
