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
    resolvers += "jitpack" at "https://jitpack.io",
    libraryDependencies ++= Seq(
      Libs.`scala-reflect`,
      Libs.`scala-compiler`,
      Akka.`akka-stream`,
      Akka.`akka-typed`,
      Akka.`akka-typed-testkit`,
      Ammonite.`ammonite`,
      Ammonite.`ammonite-sshd`,
      Libs.`jgit`,
      Libs.`enumeratum`,
      Libs.`scala-async`,
      Libs.`boopickle`,
      Covenant.`covenant-http`,
      Covenant.`covenant-ws`,
      Libs.scalaTest % Test,
    ),
    scalacOptions ++= Seq(
      "-encoding",
      "UTF-8",
      "-feature",
      "-unchecked",
      "-deprecation",
      //"-Xfatal-warnings",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Xfuture",
      //      "-Xprint:typer"
    )
  )

lazy val macros = project.settings(
  libraryDependencies ++= Seq(
    Libs.`scala-async`,
    Libs.`scala-reflect`,
  )
)
