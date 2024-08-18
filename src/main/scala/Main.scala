package com.github.mikolololoay

import zio.ZIOAppDefault
import zio.Scope
import io.getquill.*
import zio.ZIO
import zio.Console.*
import io.getquill.jdbczio.Quill
import com.github.mikolololoay.models.{Movie, Transaction}
import com.github.mikolololoay.repositories.MovieRepo
import com.github.mikolololoay.utils.CsvReader

import scala.io.Source
import zio.Chunk

import java.io.File

import kantan.csv.*
import kantan.csv.ops.*
import kantan.csv.generic.*
import com.github.mikolololoay.utils.DatabaseInitializer
import com.github.mikolololoay.repositories.TableRepo
import com.github.mikolololoay.http.HttpServer
import zio.ZLayer
import zio.http.Server


object Main extends ZIOAppDefault:
    val app =
        val quillLayer = Quill.Sqlite.fromNamingStrategy(SnakeCase)
        val dataSourceLayer = Quill.DataSource.fromPrefix("myDatabaseConfig")
        val newMovies = List(Movie("hehe", "film", 2024, "rezyser", "opis", 200))

        HttpServer.start
        .provide(
            // HTTP Layers
            ZLayer.succeed(Server.Config.default.port(HttpServer.port)),
            Server.live,
            // DB Layers
            TableRepo.layer,
            quillLayer,
            dataSourceLayer
        )

    override def run =
        app
