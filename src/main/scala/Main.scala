package com.github.mikolololoay

import com.github.mikolololoay.http.HttpServer
import com.github.mikolololoay.models.Movie
import com.github.mikolololoay.models.TicketTransaction
import com.github.mikolololoay.repositories.AggregationsRepo
import com.github.mikolololoay.repositories.SessionRepo
import com.github.mikolololoay.repositories.tablerepos.MovieRepo
import com.github.mikolololoay.repositories.tablerepos.TableRepo
import com.github.mikolololoay.utils.CsvReader
import com.github.mikolololoay.utils.DatabaseInitializer
import com.github.mikolololoay.utils.redis.ProtobufCodecSupplier
import io.getquill.*
import io.getquill.jdbczio.Quill
import kantan.csv.*
import kantan.csv.generic.*
import kantan.csv.ops.*
import zio.Chunk
import zio.Console.*
import zio.Scope
import zio.ZIO
import zio.ZIOAppDefault
import zio.ZLayer
import zio.http.Server
import zio.redis.*
import zio.schema.*
import zio.schema.codec.*

import java.io.File
import scala.io.Source


object Main extends ZIOAppDefault:
    /** The final application.
      * 
      * First it initializes the db by recreating tables related to models
      * and filling them with data from the resources/initial_csvs/.csv files.
      * Then it starts the Http service locally.
      * 
      * The app requires local postgres and redis databases running.
      */
    val app =
        val quillLayer = Quill.Postgres.fromNamingStrategy(SnakeCase)
        val dataSourceLayer = Quill.DataSource.fromPrefix("myDatabaseConfig")

        (
            DatabaseInitializer.initialize() *>
                ZIO.logInfo("Successfully initialized database.") *>
                HttpServer.start.debug
        )
            .provide(
                // HTTP Layers
                ZLayer.succeed(Server.Config.default.port(HttpServer.port)),
                Server.live,
                // DB Layers
                TableRepo.layer,
                AggregationsRepo.layer,
                quillLayer,
                dataSourceLayer,
                // Redis layers
                SessionRepo.layer,
                Redis.layer,
                RedisExecutor.local,
                ZLayer.succeed[CodecSupplier](ProtobufCodecSupplier)
            )

    override def run =
        app
