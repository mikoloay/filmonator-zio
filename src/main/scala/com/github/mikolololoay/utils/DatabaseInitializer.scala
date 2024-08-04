package com.github.mikolololoay.utils

import zio.*
import com.github.mikolololoay.models.Movie

import java.io.File
import com.github.mikolololoay.models.Ticket
import com.github.mikolololoay.models.Screening
import com.github.mikolololoay.models.ScreeningRoom
import com.github.mikolololoay.models.TicketTransaction
import com.github.mikolololoay.repositories.TableRepo
import izumi.reflect.Tag
import kantan.csv.HeaderDecoder
import java.sql.SQLException
import java.io.IOException


object DatabaseInitializer:
    def initialize() =
        ZIO.collectAllParDiscard:
            List(
                initializeTable[Movie]("src/main/resources/initial_csvs/movies.csv", '|'),
                initializeTable[ScreeningRoom]("src/main/resources/initial_csvs/screening_rooms.csv", '>'),
                initializeTable[Screening]("src/main/resources/initial_csvs/screenings.csv", ','),
                initializeTable[Ticket]("src/main/resources/initial_csvs/tickets.csv", '#'),
                initializeTable[TicketTransaction]("src/main/resources/initial_csvs/transactions.csv", ';')
            )

    private def initializeTable[A: HeaderDecoder: Tag](
            path: String,
            separator: Char
    ): ZIO[TableRepo[A], SQLException | IOException, Unit] =
        for
            _ <- TableRepo.recreateTable[A]()
            records <- CsvReader.readFromFile[A](File(path), separator)
            _ <- TableRepo.add[A](records)
        yield ()
