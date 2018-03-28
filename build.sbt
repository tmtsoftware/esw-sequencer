lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .dependsOn(macros)
  .settings(
    inThisBuild(List(
      organization := "org.tmt",
      scalaVersion := "2.12.4",
      version := "0.1.0-SNAPSHOT"
    )),
    name := "sequencer-framework",
    libraryDependencies ++= Seq(
      Libs.`scala-reflect`,
      Libs.`scala-compiler`,
      Akka.`akka-stream`,
      Akka.`akka-typed`,
      Akka.`akka-typed-testkit`,
      Ammonite.`ammonite`,
      Ammonite.`ammonite-sshd`,
      Libs.`jgit`,
      Libs.`scala-async`,
      Libs.scalaTest % Test,
    )
  )

lazy val macros = project.settings(
  libraryDependencies ++= Seq(
    Libs.`scala-async`,
    Libs.`scala-reflect`,
  )
)
