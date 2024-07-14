package com.github.mikolololoay.repositories

import zio.*
import io.getquill.*
import io.getquill.jdbczio.Quill
import java.sql.SQLException
import com.github.mikolololoay.models.Ticket
import utils.TableRepo


// trait TicketRepo:
//     def getAll: ZIO[Any, SQLException, List[Ticket]]
//     def add(ticket: Ticket): ZIO[Any, SQLException, Long]
//     def add(newTickets: List[Ticket]): ZIO[Any, SQLException, List[Long]]
//     def delete(id: String): ZIO[Any, SQLException, Long]


trait TicketRepo extends TableRepo[Ticket]

object TicketRepo:
    def getAll: ZIO[TicketRepo, SQLException, List[Ticket]] =
        ZIO.serviceWithZIO[TicketRepo](_.getAll)
    
    def add(ticket: Ticket) =
        ZIO.serviceWithZIO[TicketRepo](_.add(ticket))
    
    def add(newTickets: List[Ticket]) =
        ZIO.serviceWithZIO[TicketRepo](_.add(newTickets))

    def delete(name: String) =
        ZIO.serviceWithZIO[TicketRepo](_.delete(name))


class TicketRepoImpl(quill: Quill.Sqlite[SnakeCase]) extends TicketRepo:
    import quill.*
    override def getAll: ZIO[Any, SQLException, List[Ticket]] = run(query[Ticket])

    override def add(ticket: Ticket) = run(query[Ticket].insertValue(lift(ticket)))

    override def add(newTickets: List[Ticket]) = run:
        liftQuery(newTickets).foreach(query[Ticket].insertValue(_))

    override def delete(name: String): ZIO[Any, SQLException, Long] = run:
        query[Ticket].filter(ticket => ticket.name == lift(name)).delete


object TicketRepoImpl:
    val layer: ZLayer[Quill.Sqlite[SnakeCase], Nothing, TicketRepo] =
        ZLayer.fromFunction(quill => new TicketRepoImpl(quill))
