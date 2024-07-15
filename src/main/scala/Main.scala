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


object Main extends ZIOAppDefault:
    val app =
        val quillLayer = Quill.Sqlite.fromNamingStrategy(SnakeCase)
        val dataSourceLayer = Quill.DataSource.fromPrefix("myDatabaseConfig")
        val newMovies = List(Movie("hehe", "film", 2024, "rezyser", "opis", 200))

        (
            for
                _ <- TableRepo.delete[Movie]("2f6766a983be487f8f487b3f6f8b2f7b")


                // _ <- TableRepo.truncate[Movie]() // ROBIMY TRUNCATE

                // moviesBeforeInsert <- TableRepo.getAll[Movie]
                // _ <- printLine(moviesBeforeInsert.map(_.name)) // POBIERAMY WSZYSTKIE FILMY (JEST ICH 0!)

                // _ <- DatabaseInitializer.initialize() // DODAJEMY FILMY Z PLIKU CSV!

                // moviesAfterInsert <- TableRepo.getAll[Movie]
                // _ <- printLine(moviesAfterInsert.map(_.name)) // JEST DUZO FILMOW W BAZIE

                // _ <- TableRepo.add[Movie](newMovies)

                // moviesAfterInsert2 <- TableRepo.getAll[Movie]
                // _ <- printLine(moviesAfterInsert2.map(_.name)) // JEST DUZO FILMOW W BAZIE
                // _ <- TableRepo.delete[Movie]("hehe")
                // moviesAfterDelete <- TableRepo.getAll[Movie]
                // _ <- printLine(moviesAfterDelete.map(_.name))

                // 
                // moviesAfterInit <- TableRepo.getAll[Movie]
                // _ <- printLine(moviesAfterInit.map(_.name))
            yield ()
        )
        .provide(
            MovieRepo.layer,
            quillLayer,
            dataSourceLayer
        )

    override def run =
        app
        // DatabaseInitializer.initialize()