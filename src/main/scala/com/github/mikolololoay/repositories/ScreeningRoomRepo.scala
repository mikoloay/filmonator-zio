package com.github.mikolololoay.repositories

import zio.*
import io.getquill.*
import io.getquill.jdbczio.Quill
import java.sql.SQLException
import com.github.mikolololoay.models.ScreeningRoom


class ScreeningRoomRepo(quill: Quill.Sqlite[SnakeCase]) extends TableRepo[ScreeningRoom]:
    import quill.*

    override inline val tableName = "screening_room"

    override def getAll: ZIO[Any, SQLException, List[ScreeningRoom]] = run(query[ScreeningRoom])

    override def add(screeningRoom: ScreeningRoom) = run(query[ScreeningRoom].insertValue(lift(screeningRoom)))

    override def add(newScreeningRooms: List[ScreeningRoom]) = run:
        liftQuery(newScreeningRooms).foreach(query[ScreeningRoom].insertValue(_))

    override def delete(id: String): ZIO[Any, SQLException, Long] = run:
        query[ScreeningRoom].filter(screeningRoom => screeningRoom.id == lift(id)).delete

    def truncate() = run:
        query[ScreeningRoom].delete

object ScreeningRoomRepo:
    val layer: ZLayer[Quill.Sqlite[SnakeCase], Nothing, TableRepo[ScreeningRoom]] =
        ZLayer.fromFunction(quill => new ScreeningRoomRepo(quill))
