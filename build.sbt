name := "sbt-jaxws"

organization := "com.github.mdr"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.9.1"

sbtPlugin := true

CrossBuilding.crossSbtVersions := Seq("0.11.2", "0.11.3")

libraryDependencies += "org.glassfish.ha" % "ha-api" % "3.1.8" artifacts( 
  Artifact("ha-api", "jar", "jar") 
) 

libraryDependencies += "com.sun.xml.ws" % "jaxws-tools" % "2.2.7-promoted-b24"

publishMavenStyle := true

publishArtifact in Test := false

publishTo <<= isSnapshot(
  if (_) Some("snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/") 
  else   Some("releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2/"))

pomExtra := {
    <inceptionYear>2012</inceptionYear>
    <url>http://github.com/mdr/sbt-jaxws</url>
    <licenses>
      <license>
        <name>MIT License</name>
        <url>http://www.opensource.org/licenses/mit-license.php</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:mdr/sbt-jaxws.git</url>
      <connection>scm:git:git@github.com:mdr/sbt-jaxws</connection>
    </scm>
    <developers>
      <developer>
        <id>mdr</id>
        <name>Matt Russell</name>
        <url>https://github.com/mdr/</url>
      </developer>
    </developers>
  }

EclipseKeys.withSource := true

EclipseKeys.eclipseOutput := Some("bin")