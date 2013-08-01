import sbt._
import sbt.Keys._
import Resolver.sonatypeRepo

object Build extends Build {

  lazy val bidiatool = Project(
    "bidiatool",
    file("."),
    settings = commonSettings ++ Seq(
      libraryDependencies ++= Seq(
      )
    )
  ).configs( GUITests, PlotTests )
  .settings( inConfig(GUITests)(Defaults.testTasks): _*)
  .settings( inConfig(PlotTests)(Defaults.testTasks): _*)
  .settings(
    testOptions in GUITests := Seq(
      Tests.Argument("-n", "umich.gui.tags.GUITest")
    ),
    testOptions in PlotTests := Seq(
      Tests.Argument("-n", "umich.plot.tags.PlotTest")
    )
  )

  lazy val GUITests = config("gui") extend(Test)
  lazy val PlotTests = config("plot") extend(Test)

  //lazy val formatSettings = scalariformSettings ++ Seq(
    //ScalariformKeys.preferences in Compile := formattingPreferences,
    //ScalariformKeys.preferences in Test    := formattingPreferences
  //)

  //def formattingPreferences = {
    //import scalariform.formatter.preferences._
    //FormattingPreferences()
      //.setPreference(RewriteArrowSymbols, true)
      //.setPreference(AlignParameters, true)
      //.setPreference(AlignSingleLineCaseStatements, true)
      //.setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
      //.setPreference(CompactControlReadability, false)
  //}

  def commonSettings =
    Defaults.defaultSettings ++
    Seq(
      organization := "umich",
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
        //Dependency.Compile.cilibLibrary,
        Dependency.Compile.guava, // cilib-library dependency
        Dependency.Compile.functionalJava, // cilib-library dependency
        Dependency.Compile.parboiledCore, // cilib-library dependency
        Dependency.Compile.parboiledJava, // cilib-library dependency

        Dependency.Compile.scalazCore,
        Dependency.Compile.shapeless,
        Dependency.Compile.spireMath,
        Dependency.Compile.ejml,
        Dependency.Compile.commonsMath,
        Dependency.Compile.scalachart,
        Dependency.Compile.joglMain,
        Dependency.Compile.gluegenRt,
        Dependency.Test.scalatest,
        Dependency.Test.festSwing,
        compilerPlugin("org.scala-lang.plugins" % "continuations" % Version.scala)
      ),
      resolvers ++= Seq(
        sonatypeRepo("snapshots"),
        sonatypeRepo("releases")
      ),
      parallelExecution in Test := false
    )

  object Version {
    val scala = "2.10.2"
  }

  object Dependency {

    object Compile {
      //val cilibLibrary = "net.cilib" % "cilib-library" % "0.8-SNAPSHOT"
      val guava = "com.google.guava" % "guava" % "13.0.1"
      val functionalJava = "org.functionaljava" % "functionaljava" % "3.1"
      val parboiledCore = "org.parboiled" % "parboiled-core" % "1.1.4"
      val parboiledJava = "org.parboiled" % "parboiled-java" % "1.1.4"
      val scalazCore = "org.scalaz" % "scalaz-core_2.10" % "7.0.2"
      val shapeless = "com.chuusai" % "shapeless_2.10" % "1.2.4"
      val spireMath = "org.spire-math" % "spire_2.10" % "0.5.0"
      val ejml = "com.googlecode.efficient-java-matrix-library" % "ejml" % "0.22"
      val commonsMath = "org.apache.commons" % "commons-math3" % "3.2"
      val scalachart = "com.github.wookietreiber" % "scala-chart_2.10" % "0.2.2"
      val joglMain = "org.jogamp.jogl" % "jogl-all-main" % "2.0-rc11"
      val gluegenRt = "org.jogamp.gluegen" % "gluegen-rt-main" % "2.0-rc11"
  }

    object Test {
      val scalatest = "org.scalatest" % "scalatest_2.10" % "2.0.M6-SNAP28" % "test"
      val festSwing = "org.easytesting" % "fest-swing" % "1.2.1" % "test"
    }
  }
}
