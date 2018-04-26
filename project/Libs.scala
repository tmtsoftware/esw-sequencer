import sbt._
import Def.{setting => dep}
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import scalapb.compiler.Version.scalapbVersion

object Libs {
  val ScalaVersion = "2.12.4"

  val `scala-reflect`        = "org.scala-lang" % "scala-reflect" % ScalaVersion
  val `scala-compiler`       = "org.scala-lang" % "scala-compiler" % ScalaVersion
  val `scala-java8-compat`   = "org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0" //BSD 3-clause "New" or "Revised" License
  val `jgit`                 = "org.eclipse.jgit" % "org.eclipse.jgit" % "4.11.0.201803080745-r"
  val `scala-async`          = "org.scala-lang.modules" %% "scala-async" % "0.9.7"
  val `enumeratum`           = "com.beachape" %% "enumeratum" % "1.5.13"
  val `akka-http-cors`       = "ch.megard" %% "akka-http-cors" % "0.3.0"
  val `play-json-extensions` = dep("ai.x" %% "play-json-extensions" % "0.10.0") //Simplified BSD License
  val `play-json`            = dep("com.typesafe.play" %%% "play-json" % "2.6.9") //Apache 2.0
  val `scalajs-java-time`    = dep("org.scala-js" %%% "scalajs-java-time" % "0.2.4")
  val `scalapb-runtime`      = dep("com.thesamet.scalapb" %%% "scalapb-runtime" % scalapbVersion)
  val `scalajs-library`      = "org.scala-js" %% "scalajs-library" % "0.6.22"
  val `sequencer-scripts`    = "org.tmt" %% "sequencer-scripts" % "0.1.0-SNAPSHOT"
  val `types__mocha`         = "org.webjars.npm" % "types__mocha" % "5.2.0"
  val `mysequencer`          = "org.webjars.npm" % "mysequencer" % "1.0.0"
  val `chameleon`            = dep("com.github.cornerman.chameleon" %%% "chameleon" % "7dacc9f")
  val `monix`                = dep("io.monix" %%% "monix" % "3.0.0-RC1")
}

object Covenant {
  val Version = "da2ce5a"

  val `covenant-http` = dep("com.github.cornerman.covenant" %%% "covenant-http" % Version)
  val `covenant-ws`   = dep("com.github.cornerman.covenant" %%% "covenant-ws"   % Version)
}

object Akka {
  val Version = "2.5.11"

  val `akka-stream`        = "com.typesafe.akka" %% "akka-stream"        % Version
  val `akka-typed`         = "com.typesafe.akka" %% "akka-actor-typed"   % Version
  val `akka-http`          = "com.typesafe.akka" %% "akka-http"          % "10.1.1"
  val `akka-typed-testkit` = "com.typesafe.akka" %% "akka-testkit-typed" % Version
  val `akka-http-circe`    = "de.heikoseeberger" %% "akka-http-circe"    % "1.20.1" //Apache 2.0
}

object AkkaJs {
  val Version              = "1.2.5.11"
  val `akkajsactortyped`   = dep("org.akka-js" %%% "akkajsactortyped" % Version)
  val `akkajsactorstream`  = dep("org.akka-js" %%% "akkajsactorstream" % Version)
  val `akkajstypedtestkit` = dep("org.akka-js" %%% "akkajstypedtestkit" % Version)
}

object Ammonite {
  val Version = "1.1.0"

  val `ammonite`      = "com.lihaoyi" % "ammonite"      % Version cross CrossVersion.full
  val `ammonite-sshd` = "com.lihaoyi" % "ammonite-sshd" % Version cross CrossVersion.full
}

object SharedLibs {
  val `scalaTest` = dep("org.scalatest" %%% "scalatest" % "3.0.4")
}

object Enumeratum {
  val version                = "1.5.13"
  val `enumeratum`           = dep("com.beachape" %%% "enumeratum" % version) //MIT License
  val `enumeratum-play-json` = "com.beachape" %% "enumeratum-play-json" % version //MIT License
}

object Chill {
  val Version           = "0.9.2"
  val `chill-bijection` = "com.twitter" %% "chill-bijection" % Version //Apache License 2.0
  val `chill-akka`      = "com.twitter" %% "chill-akka" % Version //Apache License 2.0
}

object Circe {
  val Version = "0.9.3"

  val `circe-core`    = dep("io.circe" %%% "circe-core"    % Version)
  val `circe-generic` = dep("io.circe" %%% "circe-generic" % Version)
  val `circe-parser`  = dep("io.circe" %%% "circe-parser"  % Version)
}

object Sttp {
  val `sttp-core`            = dep("com.softwaremill.sttp" %%% "core" % "1.2.0-RC1")
  val `monix-backend`        = dep("com.softwaremill.sttp" %%% "monix" % "1.2.0-RC1")
  val `okhttp-backend-monix` = "com.softwaremill.sttp" %% "okhttp-backend-monix" % "1.2.0-RC1"
  val `akka-http-backend`    = "com.softwaremill.sttp" %% "akka-http-backend" % "1.2.0-RC1"
  val `circe`                = dep("com.softwaremill.sttp" %%% "circe" % "1.2.0-RC1")
}
