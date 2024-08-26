package com.github.mikolololoay

import zio.ZIOAppDefault
import zio.Scope
import io.getquill.*
import zio.ZIO
import zio.Console.*
import io.getquill.jdbczio.Quill
import com.github.mikolololoay.models.{Movie, TicketTransaction}
import com.github.mikolololoay.repositories.tablerepos.MovieRepo
import com.github.mikolololoay.utils.CsvReader

import scala.io.Source
import zio.Chunk

import java.io.File

import kantan.csv.*
import kantan.csv.ops.*
import kantan.csv.generic.*
import com.github.mikolololoay.utils.DatabaseInitializer
import com.github.mikolololoay.utils.redis.ProtobufCodecSupplier
import com.github.mikolololoay.repositories.tablerepos.TableRepo
import com.github.mikolololoay.http.HttpServer
import zio.ZLayer
import zio.http.Server
import com.github.mikolololoay.repositories.AggregationsRepo
import com.github.mikolololoay.repositories.SessionRepo
import zio.redis.*
import zio.schema.*
import zio.schema.codec.*


object Main extends ZIOAppDefault:
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
