package com.github.mikoloay.repositories.tablerepos

import zio.ZIO
import java.sql.SQLException
import izumi.reflect.Tag
import io.getquill.*
import io.getquill.SchemaMeta
import zio.ZLayer


trait TableRepo[A]:
    def getAll: ZIO[Any, SQLException, List[A]]
    def get(id: String): ZIO[Any, SQLException, List[A]]
    def add(record: A): ZIO[Any, SQLException, Long]
    def add(newRecords: List[A]): ZIO[Any, SQLException, List[Long]]
    def upsert(record: A): ZIO[Any, SQLException, Long]
    def delete(id: String): ZIO[Any, SQLException, Long]
    def truncate(): ZIO[Any, SQLException, Long]
    def recreateTable(): ZIO[Any, SQLException, Long]


object TableRepo:
    def getAll[A: Tag]: ZIO[TableRepo[A], SQLException, List[A]] =
        ZIO.serviceWithZIO[TableRepo[A]](_.getAll)

    def get[A: Tag](id: String): ZIO[TableRepo[A], SQLException, List[A]] =
        ZIO.serviceWithZIO[TableRepo[A]](_.get(id))

    def add[A: Tag](record: A): ZIO[TableRepo[A], SQLException, Long] =
        ZIO.serviceWithZIO[TableRepo[A]](_.add(record))

    def add[A: Tag](newRecords: List[A]): ZIO[TableRepo[A], SQLException, List[Long]] =
        ZIO.serviceWithZIO[TableRepo[A]](_.add(newRecords))

    def upsert[A: Tag](record: A): ZIO[TableRepo[A], SQLException, Long] =
        ZIO.serviceWithZIO[TableRepo[A]](_.upsert(record))

    def delete[A: Tag](id: String): ZIO[TableRepo[A], SQLException, Long] =
        ZIO.serviceWithZIO[TableRepo[A]](_.delete(id))

    def truncate[A: Tag](): ZIO[TableRepo[A], SQLException, Long] =
        ZIO.serviceWithZIO[TableRepo[A]](_.truncate())

    def recreateTable[A: Tag]() =
        ZIO.serviceWithZIO[TableRepo[A]](_.recreateTable())

    val layer =
        MovieRepo.layer ++
            ScreeningRepo.layer ++
            ScreeningRoomRepo.layer ++
            TicketRepo.layer ++
            TransactionRepo.layer
