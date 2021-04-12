// See README.md for license details.

ThisBuild / scalaVersion     := "2.12.13"
ThisBuild / version          := "1.0.0"
ThisBuild / transitiveClassifiers := Seq(Artifact.SourceClassifier)

lazy val root = (project in file("."))
  .settings(
    name := "SEMP_chisel",
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chisel3" % "latest.release",
      "org.easysoc" %% "layered-firrtl" % "latest.release",
      "edu.berkeley.cs" %% "chiseltest" % "latest.release",
      "edu.berkeley.cs" %% "rocketchip" % "latest.release", // rocketchipの機能を使える
      "edu.berkeley.cs" %% "dsptools" % "latest.release",     //dsptool
      "edu.berkeley.cs" % "ip-contributions" % "latest.release" // chisel ip-contributions  // import chisel.lib
    ),
    scalacOptions ++= Seq(
      "-Xsource:2.11",
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit"
    ),
    addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % "3.4.+" cross CrossVersion.full),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
  )
