import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "org.tmt",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "sequencer-framework",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      `akka-stream`,
      `akka-typed`,
      `akka-typed-testkit`,
      `scala-reflect`,
      `scala-compiler`,
      `ammonite`,
      `ammonite-sshd`,
      `jgit`,
      `reactify`,
      `scalarx`,
      `scala-async`
    )
  ).enablePlugins(JavaAppPackaging)

