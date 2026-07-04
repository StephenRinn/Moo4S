ThisBuild / organization := "io.github.stephenrinn"
ThisBuild / organizationName := "Stephen Rinn"
ThisBuild / organizationHomepage := Some(url("https://github.com/StephenRinn"))
ThisBuild / version := "1.0.0"
ThisBuild / versionScheme := Some("early-semver")

ThisBuild / publishMavenStyle := true

ThisBuild / homepage := Some(url("https://github.com/StephenRinn/Moo4S"))

ThisBuild / licenses += "Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/StephenRinn/Moo4S"),
    "scm:git:https://github.com/StephenRinn/Moo4S.git"
  )
)

ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "Moo4S",
    libraryDependencies ++= Seq {
      "org.scalatest" %% "scalatest" % "3.2.20" % Test
    },
  )

ThisBuild / developers := List(
  Developer(
    id = "StephenRinn",
    name = "Stephen Rinn",
    email = "",
    url = url("https://github.com/StephenRinn")
  )
)