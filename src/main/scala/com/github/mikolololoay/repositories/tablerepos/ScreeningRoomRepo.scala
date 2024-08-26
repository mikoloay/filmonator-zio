package com.github.mikolololoay.repositories.tablerepos

import zio.*
import io.getquill.*
import io.getquill.jdbczio.Quill
import java.sql.SQLException
import com.github.mikolololoay.models.ScreeningRoom


class ScreeningRoomRepo(quill: Quill.Postgres[SnakeCase]) extends TableRepo[ScreeningRoom]:
    import quill.*

    override def getAll: ZIO[Any, SQLException, List[ScreeningRoom]] = run(query[ScreeningRoom])

    override def get(id: String): ZIO[Any, SQLException, List[ScreeningRoom]] = run:
        query[ScreeningRoom].filter(_.id == lift(id))

    override def add(screeningRoom: ScreeningRoom) = run(query[ScreeningRoom].insertValue(lift(screeningRoom)))

    override def add(newScreeningRooms: List[ScreeningRoom]) = run:
        liftQuery(newScreeningRooms).foreach(query[ScreeningRoom].insertValue(_))

    override def upsert(screeningRoom: ScreeningRoom) =
        val id = screeningRoom.id
        val existScreeningRoomsWithId = get(id).map(_.nonEmpty)
        ZIO.ifZIO(existScreeningRoomsWithId)(
            onTrue = run:
                query[ScreeningRoom].filter(_.id == lift(id)).updateValue(lift(screeningRoom)),
            onFalse = add(screeningRoom)
        )

    override def delete(id: String): ZIO[Any, SQLException, Long] = run:
        query[ScreeningRoom].filter(_.id == lift(id)).delete

    override def truncate() = run:
        query[ScreeningRoom].delete

    override def recreateTable() =
        val dropTable = run:
            sql"drop table if exists screening_room".as[Action[ScreeningRoom]]
        val createTable = run:
            sql"""create table screening_room (
                id varchar
                ,room_name varchar
                ,capacity int
                ,has3d bool
                ,screen_type varchar
                ,audio_system varchar
            );
            """.as[Action[ScreeningRoom]]

        dropTable *> createTable


object ScreeningRoomRepo:
    val layer: ZLayer[Quill.Postgres[SnakeCase], Nothing, TableRepo[ScreeningRoom]] =
        ZLayer.fromFunction(quill => new ScreeningRoomRepo(quill))
