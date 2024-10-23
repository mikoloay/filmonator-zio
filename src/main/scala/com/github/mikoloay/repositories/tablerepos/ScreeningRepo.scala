package com.github.mikoloay.repositories.tablerepos

import zio.*
import io.getquill.*
import io.getquill.jdbczio.Quill
import java.sql.SQLException
import com.github.mikoloay.models.Screening


class ScreeningRepo(quill: Quill.Postgres[SnakeCase]) extends TableRepo[Screening]:
    import quill.*

    override def getAll: ZIO[Any, SQLException, List[Screening]] = run(query[Screening])

    override def get(screeningId: String): ZIO[Any, SQLException, List[Screening]] = run:
        query[Screening].filter(_.screeningId == lift(screeningId))

    override def add(screening: Screening) = run(query[Screening].insertValue(lift(screening)))

    override def add(newScreenings: List[Screening]) = run:
        liftQuery(newScreenings).foreach(query[Screening].insertValue(_))

    override def upsert(screening: Screening) =
        val id = screening.screeningId
        val existScreeningsWithId = get(id).map(_.nonEmpty)
        ZIO.ifZIO(existScreeningsWithId)(
            onTrue = run:
                query[Screening].filter(_.screeningId == lift(id)).updateValue(lift(screening)),
            onFalse = add(screening)
        )

    override def delete(screeningId: String): ZIO[Any, SQLException, Long] = run:
        query[Screening].filter(_.screeningId == lift(screeningId)).delete

    override def truncate() = run:
        query[Screening].delete

    override def recreateTable() =
        val dropTable = run:
            sql"drop table if exists screening".as[Action[Screening]]
        val createTable = run:
            sql"""create table screening (
                screening_id varchar
                ,room_id varchar
                ,movie_id varchar
                ,date varchar
            );
            """.as[Action[Screening]]

        dropTable *> createTable


object ScreeningRepo:
    val layer: ZLayer[Quill.Postgres[SnakeCase], Nothing, TableRepo[Screening]] =
        ZLayer.fromFunction(quill => new ScreeningRepo(quill))
