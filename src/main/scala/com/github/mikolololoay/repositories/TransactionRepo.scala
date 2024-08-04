package com.github.mikolololoay.repositories

import zio.*
import io.getquill.*
import io.getquill.jdbczio.Quill
import java.sql.SQLException
import com.github.mikolololoay.models.TicketTransaction


class TransactionRepo(quill: Quill.Postgres[SnakeCase]) extends TableRepo[TicketTransaction]:
    import quill.*

    override def getAll: ZIO[Any, SQLException, List[TicketTransaction]] = run(
        query[TicketTransaction]
    )

    override def get(id: String): ZIO[Any, SQLException, List[TicketTransaction]] =
        run:
            query[TicketTransaction].filter(transaction => transaction.id == lift(id))

    override def add(transaction: TicketTransaction) = run(
        query[TicketTransaction].insertValue(lift(transaction))
    )

    override def add(newTransactions: List[TicketTransaction]) = run:
        liftQuery(newTransactions).foreach(query[TicketTransaction].insertValue(_))

    override def delete(id: String): ZIO[Any, SQLException, Long] = run:
        query[TicketTransaction]
            .filter(transaction => transaction.id == lift(id))
            .delete

    override def truncate() = run:
        query[TicketTransaction].delete

    override def recreateTable() =
        val dropTable = run:
            sql"drop table if exists ticket_transaction".as[Action[TicketTransaction]]
        val createTable = run:
            sql"""create table ticket_transaction (
                id varchar
                ,screening_id varchar
                ,ticket_type varchar
            );
            """.as[Action[TicketTransaction]]

        dropTable *> createTable


object TransactionRepo:
    val layer: ZLayer[Quill.Postgres[SnakeCase], Nothing, TableRepo[TicketTransaction]] =
        ZLayer.fromFunction(quill => new TransactionRepo(quill))
