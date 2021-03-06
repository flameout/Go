name := "Go"

organization := "Project Go: Max Tim Christoph"

version := "0.0.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-swing" % "2.11.0-M7",
  "com.typesafe.akka" %% "akka-remote" % "2.5.6",
  "de.tuda.stg" %% "retier-core" % "0+",
  "de.tuda.stg" %% "retier-architectures-basic" % "0+",
  "de.tuda.stg" %% "retier-serializable-upickle" % "0+",
  "de.tuda.stg" %% "retier-network-tcp" % "0+",
  "de.tuda.stg" %% "retier-transmitter-basic" % "0+",
  "de.tuda.stg" %% "retier-transmitter-rescala" % "0+"
)

scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked")

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
