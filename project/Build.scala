import sbt._
import Keys._

object BuildSettings {
    val buildOrganization = "eu.balamut"
    val buildVersion      = "0-SNAPSHOT"
    val buildScalaVersion = "2.9.1"

    val buildSettings = Defaults.defaultSettings ++ Seq(
        organization  := buildOrganization,
        version       := buildVersion,
        scalaVersion  := buildScalaVersion,
        resolvers += ("sbt-idea-repo" at "http://mpeltonen.github.com/maven/"),
        scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "UTF8"),
        javacOptions  ++= Seq("-g", "-encoding", "UTF8"))
}

object Dependencies {
    val deps = Seq(
        "org.scalaz" %% "scalaz-core" % "6.0.4",
        "junit" % "junit" % "4.8.2" % "test",
        "org.scalatest" %% "scalatest" % "1.8" % "test",
        "org.scala-tools.testing" %% "specs" % "1.6.9" % "test"
    )

    val junit4SBT = "com.novocode" % "junit-interface" % "0.7" % "test->default"
}

object WS extends Build {
    import Dependencies._
    import BuildSettings._

    lazy val root =
    Project("root", file("."),
    settings = buildSettings ++ Seq(libraryDependencies ++= (deps :+junit4SBT))
    :+ (parallelExecution in Test := false)
    :+ (classDirectory in Compile <<= target in Compile apply { _ / "classes" })
    :+ (classDirectory in Test <<= target in Test apply { _ / "test-classes" })
)
}
