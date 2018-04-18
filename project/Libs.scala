import sbt._
import Def.{setting => dep}
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import scalapb.compiler.Version.scalapbVersion

object Libs {
  val ScalaVersion = "2.12.4"

  val `scala-reflect`        = "org.scala-lang"         % "scala-reflect"         % ScalaVersion
  val `scala-compiler`       = "org.scala-lang"         % "scala-compiler"        % ScalaVersion
  val `scala-java8-compat`   = "org.scala-lang.modules" %% "scala-java8-compat"   % "0.8.0" //BSD 3-clause "New" or "Revised" License
  val `jgit`                 = "org.eclipse.jgit"       % "org.eclipse.jgit"      % "4.11.0.201803080745-r"
  val `scala-async`          = "org.scala-lang.modules" %% "scala-async"          % "0.9.7"
  val `enumeratum`           = "com.beachape"           %% "enumeratum"           % "1.5.13"
  val `akka-http-cors`       = "ch.megard"              %% "akka-http-cors"       % "0.3.0"
  val `play-json-extensions` = "ai.x"                   %% "play-json-extensions" % "0.10.0" //Simplified BSD License
  val `play-json`            = "com.typesafe.play"      %% "play-json"            % "2.6.9" //Apache 2.0
  val `scalapb-runtime`      = "com.thesamet.scalapb"   %% "scalapb-runtime"      % scalapbVersion % "protobuf"
  val `scalapb-json4s`       = "com.thesamet.scalapb"   %% "scalapb-json4s"       % "0.7.0"

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
  val `akka-typed-testkit` = "com.typesafe.akka" %% "akka-testkit-typed" % Version
}

object Ammonite {
  val Version = "1.1.0"

  val `ammonite`      = "com.lihaoyi" % "ammonite"      % Version cross CrossVersion.full
  val `ammonite-sshd` = "com.lihaoyi" % "ammonite-sshd" % Version cross CrossVersion.full
}

object SharedLibs {
  val `boopickle` = dep("io.suzaku"     %%% "boopickle" % "1.3.0")
  val `scalaTest` = dep("org.scalatest" %%% "scalatest" % "3.0.4")
}

object Enumeratum {
  val version           = "1.5.13"
  val `enumeratum`      = "com.beachape" %% "enumeratum" % version //MIT License
  val `enumeratum-play` = "com.beachape" %% "enumeratum-play" % version //MIT License
}

object Chill {
  val Version           = "0.9.2"
  val `chill-bijection` = "com.twitter" %% "chill-bijection" % Version //Apache License 2.0
}
