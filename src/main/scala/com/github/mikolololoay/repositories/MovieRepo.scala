package com.github.mikolololoay.repositories

import zio.*
import io.getquill.*
import io.getquill.jdbczio.Quill
import java.sql.SQLException
import com.github.mikolololoay.models.Movie
import java.io.IOException


class MovieRepo(quill: Quill.Sqlite[SnakeCase]) extends TableRepo[Movie]:
    import quill.*

    override def getAll: ZIO[Any, SQLException, List[Movie]] = run(query[Movie])

    override def get(id: String): ZIO[Any, SQLException, List[Movie]] = run:
        query[Movie].filter(movie => movie.id == lift(id))

    override def add(movie: Movie) = run(query[Movie].insertValue(lift(movie)))

    override def add(newMovies: List[Movie]) = run:
        liftQuery(newMovies).foreach(query[Movie].insertValue(_))

    override def delete(id: String): ZIO[Any, SQLException, Long] = run:
        query[Movie].filter(movie => movie.id == lift(id)).delete

    override def truncate() = run:
        query[Movie].delete

    override def recreateTable() =
        val dropTable = run:
            sql"drop table if exists movie".as[Action[Movie]]
        val createTable = run:
            sql"""create table movie (
                id varchar
                ,name varchar
                ,year_of_production int
                ,director varchar
                ,description varchar
                ,length_in_minutes int
            )
            """.as[Action[Movie]]

        dropTable *> createTable


object MovieRepo:
    val layer: ZLayer[Quill.Sqlite[SnakeCase], Nothing, TableRepo[Movie]] =
        ZLayer.fromFunction(quill => new MovieRepo(quill))
