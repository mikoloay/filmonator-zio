package com.github.mikolololoay.repositories.tablerepos

import zio.*
import io.getquill.*
import io.getquill.jdbczio.Quill
import java.sql.SQLException
import com.github.mikolololoay.models.Ticket


class TicketRepo(quill: Quill.Postgres[SnakeCase]) extends TableRepo[Ticket]:
    import quill.*

    override def getAll: ZIO[Any, SQLException, List[Ticket]] = run(query[Ticket])

    override def get(name: String): ZIO[Any, SQLException, List[Ticket]] = run:
        query[Ticket].filter(_.name == lift(name))

    override def add(ticket: Ticket) = run(query[Ticket].insertValue(lift(ticket)))

    override def add(newTickets: List[Ticket]) = run:
        liftQuery(newTickets).foreach(query[Ticket].insertValue(_))

    override def upsert(ticket: Ticket) =
        val name = ticket.name
        val existTicketsWithId = get(name).map(_.nonEmpty)
        ZIO.ifZIO(existTicketsWithId)(
            onTrue = run:
                query[Ticket].filter(_.name == lift(name)).updateValue(lift(ticket)),
            onFalse = add(ticket)
        )

    override def delete(name: String): ZIO[Any, SQLException, Long] = run:
        query[Ticket].filter(_.name == lift(name)).delete

    override def truncate() = run:
        query[Ticket].delete

    override def recreateTable() =
        val dropTable = run:
            sql"drop table if exists ticket".as[Action[Ticket]]
        val createTable = run:
            sql"""create table ticket (
                name varchar
                ,is_discount varchar
                ,description varchar
                ,price_in_zloty int
            );
            """.as[Action[Ticket]]

        dropTable *> createTable


object TicketRepo:
    val layer: ZLayer[Quill.Postgres[SnakeCase], Nothing, TableRepo[Ticket]] =
        ZLayer.fromFunction(quill => new TicketRepo(quill))
