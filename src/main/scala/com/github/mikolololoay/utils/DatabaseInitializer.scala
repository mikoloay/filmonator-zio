package com.github.mikolololoay.utils


import zio.*
import com.github.mikolololoay.models.Movie

import java.io.File
import com.github.mikolololoay.models.Ticket
import com.github.mikolololoay.models.Screening
import com.github.mikolololoay.models.ScreeningRoom
import com.github.mikolololoay.models.Transaction


object DatabaseInitializer:
    def initialize() =
        for
            initialModels <- readInitialModelsFromCSVs()
            _ <- ZIO.foreachPar(initialModels)(models =>
                    Console.printLine(s"${models.head.getClass().getSimpleName()} - ${models.size} objects.")
                )
        yield ()

    def readInitialModelsFromCSVs() =
        ZIO.collectAllPar(
            List(
                CsvReader.readFromFile[Movie](File("src/main/resources/initial_csvs/movies.csv"), '|'),
                CsvReader.readFromFile[Screening](File("src/main/resources/initial_csvs/screenings.csv"), ','),
                CsvReader.readFromFile[ScreeningRoom](File("src/main/resources/initial_csvs/screening_rooms.csv"), '>'),
                CsvReader.readFromFile[Ticket](File("src/main/resources/initial_csvs/tickets.csv"), '#'),
                CsvReader.readFromFile[Transaction](File("src/main/resources/initial_csvs/transactions.csv"), ';')
            )
        )
    
    // private def initializeTable[A](path: String, separator: Char)(using ) =
    //     for
