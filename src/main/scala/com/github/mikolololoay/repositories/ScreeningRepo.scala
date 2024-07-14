package com.github.mikolololoay.repositories

import zio.*
import io.getquill.*
import io.getquill.jdbczio.Quill
import java.sql.SQLException
import com.github.mikolololoay.models.Screening

import utils.TableRepo

// trait ScreeningRepo:
//     def getAll: ZIO[Any, SQLException, List[Screening]]
//     def add(screening: Screening): ZIO[Any, SQLException, Long]
//     def add(newScreenings: List[Screening]): ZIO[Any, SQLException, List[Long]]
//     def delete(screeningId: String): ZIO[Any, SQLException, Long]

trait ScreeningRepo extends TableRepo[Screening]


object ScreeningRepo:
    def getAll: ZIO[ScreeningRepo, SQLException, List[Screening]] =
        ZIO.serviceWithZIO[ScreeningRepo](_.getAll)
    
    def add(screening: Screening) =
        ZIO.serviceWithZIO[ScreeningRepo](_.add(screening))
    
    def add(newScreenings: List[Screening]) =
        ZIO.serviceWithZIO[ScreeningRepo](_.add(newScreenings))

    def delete(screeningId: String) =
        ZIO.serviceWithZIO[ScreeningRepo](_.delete(screeningId))


class ScreeningRepoImpl(quill: Quill.Sqlite[SnakeCase]) extends ScreeningRepo:
    import quill.*

    override def getAll: ZIO[Any, SQLException, List[Screening]] = run(query[Screening])

    override def add(screening: Screening) = run(query[Screening].insertValue(lift(screening)))

    override def add(newScreenings: List[Screening]) = run:
        liftQuery(newScreenings).foreach(query[Screening].insertValue(_))

    override def delete(screeningId: String): ZIO[Any, SQLException, Long] = run:
        query[Screening].filter(screening => screening.screeningId == lift(screeningId)).delete


object ScreeningRepoImpl:
    val layer: ZLayer[Quill.Sqlite[SnakeCase], Nothing, ScreeningRepo] =
        ZLayer.fromFunction(quill => new ScreeningRepoImpl(quill))

