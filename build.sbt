version := "0.0.1-SNAPSHOT"

name := "sbt-jaxws"

organization := "com.github.mdr"

scalaVersion := "2.9.1"

sbtPlugin := true

publishMavenStyle := true

libraryDependencies += "org.glassfish.ha" % "ha-api" % "3.1.8" artifacts( 
  Artifact("ha-api", "jar", "jar") 
) 

libraryDependencies += "com.sun.xml.ws" % "jaxws-tools" % "2.2.7-promoted-b24"

EclipseKeys.withSource := true

EclipseKeys.eclipseOutput := Some("bin")