import sbt._
import sbt.Keys._

import bintray.Plugin._
import bintray.Keys._

object Build extends Build {

  val customBintraySettings = bintrayPublishSettings ++ Seq(
    packageLabels in bintray       := Seq("http", "web"),
    bintrayOrganization in bintray := Some("blackboxsociety"),
    repository in bintray          := "releases"
  )

  val root = Project("root", file("."))
    .settings(customBintraySettings: _*)
    .settings(
      name                  := "blackbox-http",
      organization          := "com.blackboxsociety",
      version               := "0.1.0",
      scalaVersion          := "2.11.0",
      licenses              += ("MIT", url("http://opensource.org/licenses/MIT")),
      //scalacOptions       += "-feature",
      //scalacOptions       += "-deprecation",
      scalacOptions in Test ++= Seq("-Yrangepos"),
      resolvers             += "Black Box Society Repository" at "http://dl.bintray.com/blackboxsociety/releases",
      libraryDependencies   += "com.blackboxsociety" %% "blackbox-core" % "0.1.0",
      libraryDependencies   += "com.blackboxsociety" %% "blackbox-json" % "0.2.0",
      libraryDependencies   += "com.blackboxsociety" %% "waterhouse"    % "0.3.0"
    )

}