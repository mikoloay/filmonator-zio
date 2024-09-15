package com.github.mikoloay.repositories.tablerepos

import zio.*
import zio.test.*
import zio.test.Assertion.*
import io.getquill.*
import io.getquill.jdbczio.Quill
import com.github.mikoloay.models.Movie
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres


object TableRepoSpec extends ZIOSpecDefault:
    val postgres = ZIO.attempt(EmbeddedPostgres.start())
    val dataSource = postgres.map(_.getDatabase("postgres", "postgres"))
    val quillLayer = Quill.Postgres.fromNamingStrategy(SnakeCase)
    val dataSourceLayer = ZLayer.fromZIO(dataSource)

    val spec = suite("TableRepoSpec")(
        for
            _ <- TableRepo.recreateTable[Movie]()
        yield Chunk(
            test("Add movies"):
                TableRepo.add[Movie](
                    List(
                        Movie("1", "Movie 1", 2000, "Jane Doe", "Some movie 1", 100),
                        Movie("2", "Movie 2", 2001, "John Smith", "Some movie 2", 150),
                        Movie("3", "Movie 3", 2002, "Clara Johnson", "Some movie 3", 200)
                    )
                ).map: addedRecords =>
                    assertTrue(addedRecords.size == 3)
                    && assertTrue(addedRecords.filter(_ == 1).size == 3)
            ,

            test("Add one movie"):
                TableRepo.add[Movie](Movie("4", "Movie 4", 2003, "Adam Wilson", "Some movie 4", 250))
                    .map(addedRecord => assertTrue(addedRecord == 1))
            ,
            
            test("Get movie"):
                val newMovie = Movie("5", "Movie 5", 2004, "Joan Clarks", "Some movie 5", 300)
                for
                    _ <- TableRepo.add[Movie](newMovie)
                    movies <- TableRepo.get[Movie](newMovie.id)
                yield (
                    assertTrue(movies.size == 1) && assertTrue(movies.head.name == "Movie 5")
                )
        )
    ).provideShared(
        TableRepo.layer,
        quillLayer,
        dataSourceLayer
    )
