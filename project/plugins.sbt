libraryDependencies += "org.scala-js" %% "scalajs-env-selenium" % "0.2.0"
classpathTypes += "maven-plugin"

addSbtPlugin("com.typesafe.sbt"   % "sbt-native-packager"      % "1.3.3")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "0.6.22")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.4.0")
addSbtPlugin("io.spray"           % "sbt-revolver"             % "0.9.1")
addSbtPlugin("ch.epfl.scala"      % "sbt-scalajs-bundler"      % "0.12.0")
addSbtPlugin("io.get-coursier"    % "sbt-coursier"             % "1.0.2")
addSbtPlugin("com.thesamet"       % "sbt-protoc"               % "0.99.18" exclude ("com.thesamet.scalapb", "protoc-bridge_2.10"))
addSbtPlugin("com.typesafe.sbt"   % "sbt-mocha"                % "1.1.2")
addSbtPlugin("ch.epfl.scala"      % "sbt-web-scalajs-bundler"  % "0.12.0")
addSbtPlugin("name.de-vries"      % "sbt-typescript"           % "2.6.2")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin-shaded" % "0.7.1"
