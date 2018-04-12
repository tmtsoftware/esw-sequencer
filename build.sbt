inThisBuild(List(
  organization := "org.tmt",
  scalaVersion := "2.12.4",
  version := "0.1.0-SNAPSHOT",
  resolvers += "jitpack" at "https://jitpack.io",
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
))

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .dependsOn(macros, `sequencer-api-JVM`)
  .settings(
    name := "sequencer-framework",
    libraryDependencies ++= Seq(
      Libs.`scala-reflect`,
      Libs.`akka-http-cors`,
      Libs.`scala-compiler`,
      Akka.`akka-stream`,
      Akka.`akka-typed`,
      Akka.`akka-typed-testkit`,
      Ammonite.`ammonite`,
      Ammonite.`ammonite-sshd`,
      Libs.`jgit`,
      Libs.`enumeratum`,
      Libs.`scala-async`,
      SharedLibs.`boopickle`.value,
      Covenant.`covenant-http`.value,
      Covenant.`covenant-ws`.value,
      SharedLibs.scalaTest.value % Test,
    ),
  )

lazy val macros = project.settings(
  libraryDependencies ++= Seq(
    Libs.`scala-async`,
    Libs.`scala-reflect`,
  )
)

lazy val `sequencer-api` = crossProject.crossType(CrossType.Pure)
lazy val `sequencer-api-JS` = `sequencer-api`.js
lazy val `sequencer-api-JVM` = `sequencer-api`.jvm

lazy val `sequencer-client-js` = project
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .dependsOn(`sequencer-api-JS`)
  .settings(
    useYarn := true,
    scalaJSUseMainModuleInitializer := true,
    scalacOptions += "-P:scalajs:sjsDefinedByDefault",
    libraryDependencies ++= Seq(
      SharedLibs.`boopickle`.value,
      Covenant.`covenant-http`.value,
      Covenant.`covenant-ws`.value,
      SharedLibs.scalaTest.value % Test,
    )
  )
