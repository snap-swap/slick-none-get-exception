name := "slick-none-get-exception"

organization := "com.snapswap"

version := "0.0.1"

scalaVersion := "2.11.8"

scalacOptions := Seq(
  "-feature",
  "-unchecked",
  "-deprecation",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Ywarn-unused-import",
  "-encoding",
  "UTF-8")

libraryDependencies ++= {
  val slickV = "3.2.1"
  Seq(
    "com.typesafe.slick" %% "slick" % slickV,
    "com.h2database" % "h2" % "1.4.196"
  )
}