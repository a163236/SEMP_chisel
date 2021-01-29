name := "SEMP_chisel"

version := "0.1"

scalaVersion := "2.12.13"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-language:reflectiveCalls", "-Xsource:2.11")

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases")
)

//addCompilerPlugin("edu.berkeley.cs" %% "chisel3-plugin" % "latest.release" cross CrossVersion.full)

libraryDependencies += "edu.berkeley.cs" %% "chisel3" % "latest.release"
libraryDependencies += "edu.berkeley.cs" %% "chiseltest" % "latest.release"
//libraryDependencies += "edu.berkeley.cs" %% "rocketchip" % "latest.release"
