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

lazy val `esw-sequencer` = project
  .in(file("."))
  .aggregate(
    `sequencer-api-JS`,
    `sequencer-js-app`,
    `sequencer-api-JVM`,
    `sequencer-js-client`,
    `sequencer-macros`,
    `sequencer-framework`,
    `csw-messages`,
  )

lazy val `sequencer-api` = crossProject.crossType(CrossType.Pure)
lazy val `sequencer-api-JS` = `sequencer-api`.js
lazy val `sequencer-api-JVM` = `sequencer-api`.jvm

lazy val `sequencer-js-client` = project
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .dependsOn(`sequencer-api-JS`)
  .settings(
    webpackBundlingMode := BundlingMode.LibraryOnly(),
    useYarn := true,
    scalacOptions += "-P:scalajs:sjsDefinedByDefault",
    libraryDependencies ++= Seq(
      SharedLibs.`boopickle`.value,
      Covenant.`covenant-http`.value,
      Covenant.`covenant-ws`.value,
      SharedLibs.scalaTest.value % Test,
    )
  )

lazy val `sequencer-js-app` = project
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .dependsOn(`sequencer-js-client`)
  .settings(
    useYarn := true,
    scalaJSUseMainModuleInitializer := true,
    scalacOptions += "-P:scalajs:sjsDefinedByDefault",
    libraryDependencies ++= Seq(
      SharedLibs.scalaTest.value % Test
    )
  )

lazy val `sequencer-macros` = project
  .settings(
    libraryDependencies ++= Seq(
      Libs.`scala-async`,
      Libs.`scala-reflect`,
    )
  )

lazy val `sequencer-framework` = project
  .enablePlugins(JavaAppPackaging)
  .dependsOn(`sequencer-macros`, `sequencer-api-JVM`)
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

lazy val `csw-messages` = project
  .settings(
    libraryDependencies ++= Seq(
      Libs.`scala-java8-compat`,
      Enumeratum.`enumeratum`,
      Libs.`play-json`,
      Libs.`play-json-extensions`,
      Enumeratum.`enumeratum-play`,
      Chill.`chill-bijection`,
      Libs.`scalapb-runtime`,
      Libs.`scalapb-json4s`,
      Akka.`akka-typed`,
    )
  )
  .settings(
    PB.targets in Compile := Seq(
    PB.gens.java -> (sourceManaged in Compile).value,
    scalapb.gen(javaConversions = true) -> (sourceManaged in Compile).value
  )
)
