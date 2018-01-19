lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion    = "2.5.8"

lazy val playVersion = "2.6.11"

lazy val root = (project in file(".")).
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
      "com.typesafe.play" %% "play" % playVersion,
      "com.typesafe.play" %% "play-cache" % playVersion,


      "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test
    )
  )
