import sbt._

object Dependencies {
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4"
  val `akka-stream` = "com.typesafe.akka" %% "akka-stream" % "2.5.8"
  val `akka-typed` = "com.typesafe.akka" %% "akka-typed" % "2.5.8"
  val `akka-typed-testkit` = "com.typesafe.akka" %% "akka-typed-testkit" % "2.5.8"
  val `scala-reflect` = "org.scala-lang" % "scala-reflect" % "2.12.4"
  val `scala-compiler` = "org.scala-lang" % "scala-compiler" % "2.12.4"
  val `ammonite` = "com.lihaoyi" % "ammonite" % "1.0.3" cross CrossVersion.full
  val `ammonite-sshd` = "com.lihaoyi" % "ammonite-sshd" % "1.0.3" cross CrossVersion.full
}
