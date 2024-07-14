val scala3Version = "3.4.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "filmonatorgui",
    organization := "com.github.mikolololoay",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "io.getquill" %% "quill-jdbc-zio" % "4.8.5",
      "org.xerial" % "sqlite-jdbc" % "3.28.0",
      "dev.zio" %% "zio-nio" % "2.0.2",
      ("com.nrinaudo" %% "kantan.csv-generic" % "0.7.0").cross(CrossVersion.for3Use2_13)
    )
  )
