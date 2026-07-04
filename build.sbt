ThisBuild / version := "1.0.0"

ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "Moo4S",
    libraryDependencies ++= Seq {
      "org.scalatest" %% "scalatest" % "3.2.20" % Test
    },
  )
