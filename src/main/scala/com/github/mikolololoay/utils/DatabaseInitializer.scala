package com.github.mikolololoay.utils


import zio.*
import com.github.mikolololoay.models.Movie

import java.io.File
import com.github.mikolololoay.models.Ticket
import com.github.mikolololoay.models.Screening
import com.github.mikolololoay.models.ScreeningRoom
import com.github.mikolololoay.models.Transaction
import com.github.mikolololoay.repositories.TableRepo
import izumi.reflect.Tag
import kantan.csv.HeaderDecoder


object DatabaseInitializer:
    def initialize() =
        ZIO.collectAllParDiscard:
            List(
                initializeTable[Movie]("src/main/resources/initial_csvs/movies_broken.csv", '|')
            )
    
    private def initializeTable[A: HeaderDecoder : Tag](path: String, separator: Char) =
        for
            records <- CsvReader.readFromFile[A](File(path), separator)
            _ <- TableRepo.add[A](records)
        yield ()
