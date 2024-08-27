ThisBuild / organization := "com.github.mikolololoay"
ThisBuild / name := "filmonator-zio"
ThisBuild / scalaVersion := "3.5.0"
ThisBuild / version := "0.1.0-SNAPSHOT"


val zioVersion = "2.1.6"
val zioRedisVersion = "0.2.0"
val zioTestVersion = "2.1.8"
val postgresVersion = "42.7.3"
val munitVersion = "1.0.0"
val quillZioVersion = "4.8.5"
val kantanCsvVersion = "0.7.0"
val tapirVersion = "1.10.15"
val scalatagsVersion = "0.13.1"
val slf4jVersion = "2.0.13"
val scalaLoggingVersion = "3.9.5"
val embeddedPostgresVersion = "2.0.7"


lazy val root = project
  .in(file("."))
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-redis" % zioRedisVersion,
      "io.getquill" %% "quill-jdbc-zio" % quillZioVersion,
      "org.postgresql" % "postgresql" % postgresVersion,
      ("com.nrinaudo" %% "kantan.csv-generic" % kantanCsvVersion).cross(CrossVersion.for3Use2_13),
      "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-zio" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
      "com.lihaoyi" %% "scalatags" % scalatagsVersion,
      "org.slf4j" % "slf4j-api" % slf4jVersion,
      "org.slf4j" % "slf4j-simple" % slf4jVersion,
      // Test deps
      "org.scalameta" %% "munit" % munitVersion % Test,
      "dev.zio" %% "zio-test" % zioTestVersion % Test,
      "dev.zio" %% "zio-test-sbt" % zioTestVersion % Test,
      "dev.zio" %% "zio-test-magnolia" % zioTestVersion % Test,
      "io.zonky.test" % "embedded-postgres" % embeddedPostgresVersion % Test
    ),
  )
