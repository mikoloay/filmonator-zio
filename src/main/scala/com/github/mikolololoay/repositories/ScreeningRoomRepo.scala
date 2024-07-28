package com.github.mikolololoay.repositories

import zio.*
import io.getquill.*
import io.getquill.jdbczio.Quill
import java.sql.SQLException
import com.github.mikolololoay.models.ScreeningRoom


class ScreeningRoomRepo(quill: Quill.Sqlite[SnakeCase]) extends TableRepo[ScreeningRoom]:
    import quill.*

    override def getAll: ZIO[Any, SQLException, List[ScreeningRoom]] = run(query[ScreeningRoom])

    override def get(id: String): ZIO[Any, SQLException, List[ScreeningRoom]] = run:
        query[ScreeningRoom].filter(screeningRoom => screeningRoom.id == lift(id))

    override def add(screeningRoom: ScreeningRoom) = run(query[ScreeningRoom].insertValue(lift(screeningRoom)))

    override def add(newScreeningRooms: List[ScreeningRoom]) = run:
        liftQuery(newScreeningRooms).foreach(query[ScreeningRoom].insertValue(_))

    override def delete(id: String): ZIO[Any, SQLException, Long] = run:
        query[ScreeningRoom].filter(screeningRoom => screeningRoom.id == lift(id)).delete

    override def truncate() = run:
        query[ScreeningRoom].delete

    override def recreateTable() = run:
        sql"""create table screening_room (
            id varchar
            ,room_name varchar
            ,capacity int
            has_3d bool
            ,screen_type varchar
            ,audio_system varchar
        );
        """.as[Action[ScreeningRoom]]


object ScreeningRoomRepo:
    val layer: ZLayer[Quill.Sqlite[SnakeCase], Nothing, TableRepo[ScreeningRoom]] =
        ZLayer.fromFunction(quill => new ScreeningRoomRepo(quill))
