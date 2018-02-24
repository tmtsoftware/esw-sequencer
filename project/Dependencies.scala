import sbt._

object Dependencies {
  val scalaTest            = "org.scalatest" %% "scalatest" % "3.0.4"
  private val AkkaVersion  = "2.5.10"
  val `akka-stream`        = "com.typesafe.akka" %% "akka-stream" % AkkaVersion
  val `akka-typed`         = "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion
  val `akka-typed-testkit` = "com.typesafe.akka" %% "akka-testkit-typed" % AkkaVersion
  val `scala-reflect`      = "org.scala-lang" % "scala-reflect" % "2.12.4"
  val `scala-compiler`     = "org.scala-lang" % "scala-compiler" % "2.12.4"
  val `ammonite`           = "com.lihaoyi" % "ammonite" % "1.0.3" cross CrossVersion.full
  val `ammonite-sshd`      = "com.lihaoyi" % "ammonite-sshd" % "1.0.3" cross CrossVersion.full
}
