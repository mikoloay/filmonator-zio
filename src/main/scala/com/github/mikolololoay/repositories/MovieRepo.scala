package com.github.mikolololoay.repositories

import zio.*
import io.getquill.*
import io.getquill.jdbczio.Quill
import java.sql.SQLException
import com.github.mikolololoay.models.Movie

import utils.TableRepo


// trait MovieRepo:
//     def getAll: ZIO[Any, SQLException, List[Movie]]
//     def add(movie: Movie): ZIO[Any, SQLException, Long]
//     def add(newMovies: List[Movie]): ZIO[Any, SQLException, List[Long]]
//     def delete(id: String): ZIO[Any, SQLException, Long]


trait MovieRepo extends TableRepo[Movie]


object MovieRepo:
    def getAll: ZIO[MovieRepo, SQLException, List[Movie]] =
        ZIO.serviceWithZIO[MovieRepo](_.getAll)
    
    def add(movie: Movie) =
        ZIO.serviceWithZIO[MovieRepo](_.add(movie))
    
    def add(newMovies: List[Movie]) =
        ZIO.serviceWithZIO[MovieRepo](_.add(newMovies))

    def delete(id: String) =
        ZIO.serviceWithZIO[MovieRepo](_.delete(id))


class MovieRepoImpl(quill: Quill.Sqlite[SnakeCase]) extends MovieRepo:
    import quill.*

    override def getAll: ZIO[Any, SQLException, List[Movie]] = run(query[Movie])

    override def add(movie: Movie) = run(query[Movie].insertValue(lift(movie)))

    override def add(newMovies: List[Movie]) = run:
        liftQuery(newMovies).foreach(query[Movie].insertValue(_))

    override def delete(id: String): ZIO[Any, SQLException, Long] = run:
        query[Movie].filter(movie => movie.id == lift(id)).delete


object MovieRepoImpl:
    val layer: ZLayer[Quill.Sqlite[SnakeCase], Nothing, MovieRepo] =
        ZLayer.fromFunction(quill => new MovieRepoImpl(quill))
