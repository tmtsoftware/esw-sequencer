import com.typesafe.sbt.web.Import.WebKeys.webJarsNodeModulesDirectory
import sbtcrossproject.{CrossType, crossProject}

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
    `sequencer-api-js`,
    `sequencer-api-jvm`,
    `sequencer-js-app`,
    `sequencer-js-client`,
    `sequencer-macros`,
    `sequencer-framework`,
    `csw-messages-js`,
    `csw-messages-jvm`,
	  `sequencer-client-js`,
    `sequencer-client-jvm`
  )

lazy val `sequencer-api` = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(
    libraryDependencies ++= Seq(
      Circe.`circe-core`.value,
      Circe.`circe-generic`.value,
      Circe.`circe-parser`.value,
      SharedLibs.scalaTest.value % Test,
    )
  )

lazy val `sequencer-api-js` = `sequencer-api`.js
lazy val `sequencer-api-jvm` = `sequencer-api`.jvm

lazy val `sequencer-js-client` = project
  .enablePlugins(ScalaJSBundlerPlugin, ScalaJSWeb)
  .dependsOn(`sequencer-api-js`)
  .settings(
    webpackBundlingMode := BundlingMode.LibraryOnly(),
    Compile / npmUpdate / crossTarget := baseDirectory.value / "bundler" / "main",
    Test / npmUpdate / crossTarget := baseDirectory.value / "bundler" / "test",
    version in webpack := "4.6.0",
    scalacOptions += "-P:scalajs:sjsDefinedByDefault",
    libraryDependencies ++= Seq(
      Circe.`circe-core`.value,
      Circe.`circe-generic`.value,
      Circe.`circe-parser`.value,
      Covenant.`covenant-http`.value,
      Covenant.`covenant-ws`.value,
      SharedLibs.scalaTest.value % Test,
    )
  )


lazy val `sequencer-client` = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .dependsOn(`sequencer-api`)
  .settings(
    libraryDependencies ++= Seq(
      Circe.`circe-core`.value,
      Circe.`circe-generic`.value,
      Circe.`circe-parser`.value,
      Sttp.`sttp-core`.value,
      Libs.`monix`.value,
      Sttp.`circe`.value,
      SharedLibs.scalaTest.value % Test,
    )
  )

lazy val `sequencer-client-js` = `sequencer-client`.js
lazy val `sequencer-client-jvm` = `sequencer-client`.jvm

lazy val `sequencer-js-app` = project
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(`sequencer-js-client`, `sequencer-client-js`)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalacOptions += "-P:scalajs:sjsDefinedByDefault",
    libraryDependencies ++= Seq(
      SharedLibs.scalaTest.value % Test,
      Sttp.`monix-backend`.value
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
  .dependsOn(`sequencer-macros`, `sequencer-api-jvm`, `sequencer-client-jvm`)
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
      Libs.`akka-http-cors`,
      Akka.`akka-http`,
      Akka.`akka-http-circe`,
      Circe.`circe-core`.value,
      Circe.`circe-generic`.value,
      Circe.`circe-parser`.value,
      Covenant.`covenant-http`.value,
      Covenant.`covenant-ws`.value,
      SharedLibs.scalaTest.value % Test,
      Sttp.`akka-http-backend`
    )
  )

lazy val `csw-messages` = crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure)
  .settings(
    libraryDependencies ++= Seq(
      Enumeratum.`enumeratum`.value,
      Libs.`play-json`.value,
      Libs.`play-json-extensions`.value,
      Libs.`scalapb-runtime`.value,
      Libs.`scalapb-runtime`.value % "protobuf",
      SharedLibs.scalaTest.value % Test
    ),
    PB.protoSources in Compile := Seq(file("csw-messages/src/main/protobuf"))
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      AkkaJs.`akkajsactortyped`.value % Provided,
      AkkaJs.`akkajsactorstream`.value % Provided,
      AkkaJs.`akkajstypedtestkit`.value % Test,
      Libs.`scalajs-java-time`.value
    ),
    PB.targets in Compile := Seq(
      scalapb.gen() -> (sourceManaged in Compile).value
    )
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      Akka.`akka-typed`,
      Akka.`akka-stream`,
      Libs.`scalajs-library`,
      Enumeratum.`enumeratum-play-json`,
      Libs.`scala-java8-compat`,
      Chill.`chill-bijection`,
      Chill.`chill-akka`,
      Akka.`akka-typed-testkit` % Test,
    ),
    PB.targets in Compile := Seq(
      PB.gens.java -> (sourceManaged in Compile).value,
      scalapb.gen(javaConversions = true) -> (sourceManaged in Compile).value
    )
  )

lazy val `csw-messages-js` = `csw-messages`.js
lazy val `csw-messages-jvm` = `csw-messages`.jvm
