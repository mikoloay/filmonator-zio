package com.github.mikolololoay.repositories.utils


import zio.*
import java.sql.SQLException


trait TableRepo[A]:
    def getAll: ZIO[Any, SQLException, List[A]]
    def add(record: A): ZIO[Any, SQLException, Long]
    def add(newRecords: List[A]): ZIO[Any, SQLException, List[Long]]
    def delete(id: String): ZIO[Any, SQLException, Long]
