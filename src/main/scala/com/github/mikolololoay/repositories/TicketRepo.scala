package com.github.mikolololoay.repositories

import zio.*
import io.getquill.*
import io.getquill.jdbczio.Quill
import java.sql.SQLException
import com.github.mikolololoay.models.Ticket


class TicketRepo(quill: Quill.Sqlite[SnakeCase]) extends TableRepo[Ticket]:
    import quill.*

    override inline val tableName = "ticket"

    override def getAll: ZIO[Any, SQLException, List[Ticket]] = run(query[Ticket])

    override def add(ticket: Ticket) = run(query[Ticket].insertValue(lift(ticket)))

    override def add(newTickets: List[Ticket]) = run:
        liftQuery(newTickets).foreach(query[Ticket].insertValue(_))

    override def delete(name: String): ZIO[Any, SQLException, Long] = run:
        query[Ticket].filter(ticket => ticket.name == lift(name)).delete

    def truncate() = run:
        query[Ticket].delete

object TicketRepo:
    val layer: ZLayer[Quill.Sqlite[SnakeCase], Nothing, TableRepo[Ticket]] =
        ZLayer.fromFunction(quill => new TicketRepo(quill))
