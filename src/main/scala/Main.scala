package com.github.mikoloay

import com.github.mikoloay.http.HttpServer
import com.github.mikoloay.models.Movie
import com.github.mikoloay.models.TicketTransaction
import com.github.mikoloay.repositories.AggregationsRepo
import com.github.mikoloay.repositories.SessionRepo
import com.github.mikoloay.repositories.tablerepos.MovieRepo
import com.github.mikoloay.repositories.tablerepos.TableRepo
import com.github.mikoloay.utils.CsvReader
import com.github.mikoloay.utils.DatabaseInitializer
import com.github.mikoloay.utils.redis.ProtobufCodecSupplier
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
