import sbt._
import Keys._

object SkeletonBuild extends Build {

    val sharedSettings = Project.defaultSettings ++ Seq(
        organization        := "eu.balamut",
        version             := "1",
        scalaVersion        := "2.10.2",

        libraryDependencies ++= Seq(
            "org.scalaz" %% "scalaz-core" % "7.0.2",
            "org.scalatest" %% "scalatest" % "2.0.M5b" % "test"
        ),

        libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _),

        resolvers       ++= Seq(
            "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
            "releases"  at "http://oss.sonatype.org/content/repositories/releases",
            "Concurrent Maven Repo" at "http://conjars.org/repo"
        ),

        scalacOptions   ++= Seq(
              "-g:vars",
              "-unchecked", "-deprecation",
              "-encoding", "UTF8",
              "-feature",
              "-language:implicitConversions", "-language:postfixOps",
              "-Xfatal-warnings",
              "-target:jvm-1.6"),

        javacOptions    ++= Seq(
              "-g", "-encoding", "UTF8",
              "-Xlint:all", "-Xlint:-serial", "-Xlint:-path",
              "-Werror",
              "-Xlint:-options", "-source", "6", "-target", "6"),

        publishMavenStyle := true,

        publishArtifact in Test := true,
        publishArtifact in packageDoc := false,
        publishArtifact in (Test, packageDoc) := false,

        pomIncludeRepository := { x => false },

        pomExtra := (
            <url>https://github.com/lbalamut/sbt-skeleton</url>
            <scm>
                <url>git@github.com:lbalamut/sbt-skeleton.git</url>
                <connection>scm:git:git@github.com:lbalamut/sbt-skeleton.git</connection>
            </scm>
            <developers>
                <developer>
                    <id>lbalamut</id>
                    <name>Lukasz Balamut</name>
                    <url>http://twitter.com/lbalamut</url>
                </developer>
            </developers>)
    )

    lazy val core = Project(
        id = "root",
        base = file("."),
        settings = sharedSettings
    )
}
