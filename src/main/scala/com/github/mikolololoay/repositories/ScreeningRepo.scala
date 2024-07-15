package com.github.mikolololoay.repositories

import zio.*
import io.getquill.*
import io.getquill.jdbczio.Quill
import java.sql.SQLException
import com.github.mikolololoay.models.Screening


class ScreeningRepo(quill: Quill.Sqlite[SnakeCase]) extends TableRepo[Screening]:
    import quill.*


    // inline given metaSchema: SchemaMeta[Screening] = schemaMeta[Screening]("hehe")\

    override inline val tableName = "screening"

    override def getAll: ZIO[Any, SQLException, List[Screening]] = run(query[Screening])

    override def add(screening: Screening) = run(query[Screening].insertValue(lift(screening)))

    override def add(newScreenings: List[Screening]) = run:
        liftQuery(newScreenings).foreach(query[Screening].insertValue(_))

    override def delete(screeningId: String): ZIO[Any, SQLException, Long] = run:
        query[Screening].filter(screening => screening.screeningId == lift(screeningId)).delete

    def truncate() = run:
        query[Screening].delete


object ScreeningRepo:
    val layer: ZLayer[Quill.Sqlite[SnakeCase], Nothing, TableRepo[Screening]] =
        ZLayer.fromFunction(quill => new ScreeningRepo(quill))
