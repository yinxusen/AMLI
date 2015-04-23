import sbt._
import Keys._
import sbtassembly.Plugin._

object MyBuild extends Build {

  val versionOfScala = "2.10.4"

  lazy val demo = project.in(file("."))
    .settings(name := "demo", version := "0.1", scalaVersion := versionOfScala)
    .settings(assemblySettings: _*)
    .aggregate(core, datalyze)
    .dependsOn(core, datalyze)

  val commonSharingLibs = Seq(
    "org.apache.spark" %% "spark-core"    % "1.1.0"  % "provided",
    "org.apache.spark" %% "spark-mllib"   % "1.1.0"  % "provided"
  )

  lazy val core = project.in(file("core")).settings(
    scalaVersion := versionOfScala,
    libraryDependencies ++= Seq(
      "io.prediction" %% "core" % "0.8.1" % "provided"
    ) ++ commonSharingLibs
  ).dependsOn(datalyze)

  lazy val datalyze = project.in(file("datalyze")).settings(
    scalaVersion := versionOfScala,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.0" % "test",
      "org.apache.derby" % "derby" % "10.11.1.1" % "test",
      "org.apache.spark" % "spark-graphx_2.10" % "1.1.0" % "provided",
      "org.apache.spark" % "spark-sql_2.10" % "1.1.0" % "provided",
      "mysql" % "mysql-connector-java" % "5.1.28",
      "com.github.scopt" %% "scopt" % "3.2.0"
    ) ++ commonSharingLibs
  )
}

