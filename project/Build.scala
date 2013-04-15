import sbt._
import Keys._

object SkeletonBuild extends Build {

    val sharedSettings = Project.defaultSettings ++ Seq(
        organization        := "eu.balamut",
        version             := "0-SNAPSHOT",
        scalaVersion        := "2.10.1",
        crossScalaVersions  := Seq("2.9.2", "2.10.1"),

        libraryDependencies ++= Seq(
            "org.scalaz" %% "scalaz-core" % "7.0.0-M9",
            "org.scalatest" %% "scalatest" % "1.9.1" % "test"
        ),

        resolvers       ++= Seq(
            "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
            "releases"  at "http://oss.sonatype.org/content/repositories/releases",
            "Concurrent Maven Repo" at "http://conjars.org/repo"
        ),

        parallelExecution in Test := false,

        scalacOptions   ++= Seq("-g:vars", "-unchecked", "-deprecation", "-encoding", "UTF8", "-feature", "-language:implicitConversions", "-language:postfixOps", "-Xfatal-warnings"),
        javacOptions    ++= Seq("-g", "-encoding", "UTF8", "-Xlint:all", "-Xlint:-serial", "-Xlint:-path", "-Werror"),

        publishMavenStyle := true,

        publishArtifact in Test := false,

        pomIncludeRepository := { x => false },

        pomExtra := (
            <url>https://github.com/twitter/scalding</url>
            <licenses>
                <license>
                    <name>Apache 2</name>
                    <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
                    <distribution>repo</distribution>
                </license>
            </licenses>
            <scm>
                <url>git@github.com:lbalamut/sbt-skeleton.git</url>
                <connection>scm:git:git@github.com:lbalamut/sbt-skeleton.git</connection>
            </scm>
            <developers>
                <developer>
                    <id>posco</id>
                    <name>Lukasz Balamut</name>
                    <url>http://twitter.com/posco</url>
                </developer>
            </developers>)
    )

    lazy val root =
        Project(
            id = "root",
            base = file("."),
            settings = sharedSettings
        )
}
