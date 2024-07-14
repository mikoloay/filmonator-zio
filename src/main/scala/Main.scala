package com.github.mikolololoay

import zio.ZIOAppDefault
import zio.Scope
import io.getquill.*
import zio.ZIO
import zio.Console.*
import io.getquill.jdbczio.Quill
import com.github.mikolololoay.models.{Movie, Transaction}
import com.github.mikolololoay.repositories.{MovieRepoImpl, MovieRepo}
import com.github.mikolololoay.utils.CsvReader

import scala.io.Source
import zio.Chunk

import java.io.File

import kantan.csv.*
import kantan.csv.ops.*
import kantan.csv.generic.*
import com.github.mikolololoay.utils.DatabaseInitializer


object Main extends ZIOAppDefault:
    val app =
        val quillLayer = Quill.Sqlite.fromNamingStrategy(SnakeCase)
        val dataSourceLayer = Quill.DataSource.fromPrefix("myDatabaseConfig")
        val newMovies = List(Movie("hehe", "film", 2024, "rezyser", "opis", 200))
        
        (
            for
                moviesBefore <- MovieRepo.getAll
                _ <- printLine(moviesBefore.map(_.name))
                _ <- MovieRepo.add(newMovies)
                moviesAfterInsert <- MovieRepo.getAll
                
                _ <- printLine(moviesAfterInsert.map(_.name))
                _ <- MovieRepo.delete("hehe")
                moviesAfterDelete <- MovieRepo.getAll
                _ <- printLine(moviesAfterDelete.map(_.name))
            yield ()
        ).provide(
            MovieRepoImpl.layer,
            quillLayer,
            dataSourceLayer
        )

    override def run =
        DatabaseInitializer.initialize()