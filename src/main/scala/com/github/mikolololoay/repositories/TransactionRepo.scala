package com.github.mikolololoay.repositories

import zio.*
import io.getquill.*
import io.getquill.jdbczio.Quill
import java.sql.SQLException
import com.github.mikolololoay.models.Transaction
import utils.TableRepo


// trait TransactionRepo:
//     def getAll: ZIO[Any, SQLException, List[Transaction]]
//     def add(transaction: Transaction): ZIO[Any, SQLException, Long]
//     def add(newTransactions: List[Transaction]): ZIO[Any, SQLException, List[Long]]
//     def delete(id: String): ZIO[Any, SQLException, Long]

trait TransactionRepo extends TableRepo[Transaction]

object TransactionRepo:
    def getAll: ZIO[TransactionRepo, SQLException, List[Transaction]] =
        ZIO.serviceWithZIO[TransactionRepo](_.getAll)
    
    def add(transaction: Transaction) =
        ZIO.serviceWithZIO[TransactionRepo](_.add(transaction))
    
    def add(newTransactions: List[Transaction]) =
        ZIO.serviceWithZIO[TransactionRepo](_.add(newTransactions))

    def delete(id: String) =
        ZIO.serviceWithZIO[TransactionRepo](_.delete(id))


class TransactionRepoImpl(quill: Quill.Sqlite[SnakeCase]) extends TransactionRepo:
    import quill.*

    override def getAll: ZIO[Any, SQLException, List[Transaction]] = run(query[Transaction])

    override def add(transaction: Transaction) = run(query[Transaction].insertValue(lift(transaction)))

    override def add(newTransactions: List[Transaction]) = run:
        liftQuery(newTransactions).foreach(query[Transaction].insertValue(_))

    override def delete(id: String): ZIO[Any, SQLException, Long] = run:
        query[Transaction].filter(transaction => transaction.id == lift(id)).delete


object TransactionRepoImpl:
    val layer: ZLayer[Quill.Sqlite[SnakeCase], Nothing, TransactionRepo] =
        ZLayer.fromFunction(quill => new TransactionRepoImpl(quill))

