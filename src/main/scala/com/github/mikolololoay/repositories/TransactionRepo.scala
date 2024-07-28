package com.github.mikolololoay.repositories

import zio.*
import io.getquill.*
import io.getquill.jdbczio.Quill
import java.sql.SQLException
import com.github.mikolololoay.models.Transaction


class TransactionRepo(quill: Quill.Sqlite[SnakeCase]) extends TableRepo[Transaction]:
    import quill.*

    override def getAll: ZIO[Any, SQLException, List[Transaction]] = run(
        query[Transaction]
    )

    override def get(id: String): ZIO[Any, SQLException, List[Transaction]] =
        run:
            query[Transaction].filter(transaction => transaction.id == lift(id))

    override def add(transaction: Transaction) = run(
        query[Transaction].insertValue(lift(transaction))
    )

    override def add(newTransactions: List[Transaction]) = run:
        liftQuery(newTransactions).foreach(query[Transaction].insertValue(_))

    override def delete(id: String): ZIO[Any, SQLException, Long] = run:
        query[Transaction]
            .filter(transaction => transaction.id == lift(id))
            .delete

    override def truncate() = run:
        query[Transaction].delete

    override def recreateTable() = run:
        sql"""create table transaction (
            id varchar
            ,screening_id varchar
            ,ticket_type varchar
        );
        """.as[Action[Transaction]]


object TransactionRepo:
    val layer: ZLayer[Quill.Sqlite[SnakeCase], Nothing, TableRepo[Transaction]] =
        ZLayer.fromFunction(quill => new TransactionRepo(quill))
