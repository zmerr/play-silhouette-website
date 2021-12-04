import com.typesafe.sbt.SbtScalariform._

import scalariform.formatter.preferences._

val SlickVersion = "3.3.3"

name := "website"

version := "9.0.1"

val SilhouetteVersion = "7.0.2"

val  PostgreSQLJDBCVersion = "42.3.1"

val PlayVersion = "2.8.11"

scalaVersion := "2.13.7"

resolvers += Resolver.jcenterRepo

resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

//addDependencyTreePlugin



libraryDependencies ++= Seq(
  jdbc,
  "org.postgresql" % "postgresql" % PostgreSQLJDBCVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % SlickVersion,
  "io.github.honeycomb-cheesecake" %% "play-silhouette" % SilhouetteVersion,
  "io.github.honeycomb-cheesecake" %% "play-silhouette-password-bcrypt" % SilhouetteVersion,
  "io.github.honeycomb-cheesecake" %%  "play-silhouette-persistence" % SilhouetteVersion,
  "io.github.honeycomb-cheesecake" %%  "play-silhouette-crypto-jca" % SilhouetteVersion,
  "io.github.honeycomb-cheesecake" %%  "play-silhouette-totp" % SilhouetteVersion,
  "org.webjars" %% "webjars-play" % "2.8.8-1",
  "org.webjars" % "bootstrap" % "5.1.3" exclude("org.webjars", "jquery"),
  "org.webjars" % "jquery" % "3.6.0",
  "net.codingwell" %% "scala-guice" % "5.0.2",
  "com.iheart" %% "ficus" % "1.5.1",
  "com.typesafe.play" %% "play-mailer" % "8.0.1",
  "com.typesafe.play" %% "play-mailer-guice" % "8.0.1",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.9.2-akka-2.6.x",
  "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B4",
  "io.github.honeycomb-cheesecake" %%  "play-silhouette-testkit" % SilhouetteVersion,
  specs2 % Test,
  ehcache,
  guice,
  jdbc,
  filters
)

//libraryDependencySchemes += "org.scala-lang.modules" %% "scala-java8-compat" % "always"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

routesImport += "utils.route.Binders._"

// https://github.com/playframework/twirl/issues/105
TwirlKeys.templateImports := Seq()

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  //"-Xlint", // Enable recommended additional warnings.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  // Play has a lot of issues with unused imports and unsued params
  // https://github.com/playframework/playframework/issues/6690
  // https://github.com/playframework/twirl/issues/105
  "-Xlint:-unused,_"
)

//********************************************************
// Scalariform settings
//********************************************************

scalariformAutoformat := true

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(FormatXml, false)
  .setPreference(DoubleIndentConstructorArguments, false)
  .setPreference(DanglingCloseParenthesis, Preserve)
