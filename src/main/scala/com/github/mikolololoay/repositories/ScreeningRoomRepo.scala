package com.github.mikolololoay.repositories

import zio.*
import io.getquill.*
import io.getquill.jdbczio.Quill
import java.sql.SQLException
import com.github.mikolololoay.models.ScreeningRoom
import utils.TableRepo


// trait ScreeningRoomRepo:
//     def getAll: ZIO[Any, SQLException, List[ScreeningRoom]]
//     def add(screeningRoom: ScreeningRoom): ZIO[Any, SQLException, Long]
//     def add(newScreeningRooms: List[ScreeningRoom]): ZIO[Any, SQLException, List[Long]]
//     def delete(id: String): ZIO[Any, SQLException, Long]


trait ScreeningRoomRepo extends TableRepo[ScreeningRoom]

object ScreeningRoomRepo:
    def getAll: ZIO[ScreeningRoomRepo, SQLException, List[ScreeningRoom]] =
        ZIO.serviceWithZIO[ScreeningRoomRepo](_.getAll)
    
    def add(screeningRoom: ScreeningRoom) =
        ZIO.serviceWithZIO[ScreeningRoomRepo](_.add(screeningRoom))
    
    def add(newScreeningRooms: List[ScreeningRoom]) =
        ZIO.serviceWithZIO[ScreeningRoomRepo](_.add(newScreeningRooms))

    def delete(id: String) =
        ZIO.serviceWithZIO[ScreeningRoomRepo](_.delete(id))


class ScreeningRoomRepoImpl(quill: Quill.Sqlite[SnakeCase]) extends ScreeningRoomRepo:
    import quill.*

    override def getAll: ZIO[Any, SQLException, List[ScreeningRoom]] = run(query[ScreeningRoom])

    override def add(screeningRoom: ScreeningRoom) = run(query[ScreeningRoom].insertValue(lift(screeningRoom)))

    override def add(newScreeningRooms: List[ScreeningRoom]) = run:
        liftQuery(newScreeningRooms).foreach(query[ScreeningRoom].insertValue(_))

    override def delete(id: String): ZIO[Any, SQLException, Long] = run:
        query[ScreeningRoom].filter(screeningRoom => screeningRoom.id == lift(id)).delete


object ScreeningRoomRepoImpl:
    val layer: ZLayer[Quill.Sqlite[SnakeCase], Nothing, ScreeningRoomRepo] =
        ZLayer.fromFunction(quill => new ScreeningRoomRepoImpl(quill))
