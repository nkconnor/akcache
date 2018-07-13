import com.typesafe.sbt.SbtMultiJvm.multiJvmSettings

// POM settings for Sonatype
organization := "com.nconnor"
homepage := Some(url("https://github.com/nkconnor/akcache"))
scmInfo := Some(ScmInfo(url("https://github.com/nkconnor/akcache"), "git@github.com:nkconnor/akcache.git"))

developers := List(
  Developer("nkconnor", "Nicholas Connor", "email@nconnor.com", url("https://github.com/nkconnor"))
)

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
publishMavenStyle := true

// Add sonatype repository settings
publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)


lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion    = "2.5.8"
lazy val playVersion = "2.6.15"

lazy val root = (project in file(".")).
  settings(multiJvmSettings: _*).
  settings(
    inThisBuild(List(
      organization    := "com.nconnor.akcache",
      scalaVersion    := "2.12.4"
    )),
    name := "akcache",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
      "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,

      // Play Framework
      // https://mvnrepository.com/artifact/com.typesafe.play/play-cache
      "com.typesafe.play" %% "play-cache" % playVersion,


      "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test
    )).

  configs(MultiJvm)

