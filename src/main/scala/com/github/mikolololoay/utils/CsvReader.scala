package com.github.mikolololoay.utils

import kantan.csv.*
import kantan.csv.ops.*
import kantan.csv.generic.*
import java.io.File
import zio.*
import kantan.codecs.resource.ResourceIterator

object CsvReader:
    def readFromFile[T: HeaderDecoder](file: File, separator: Char) =
        val readerZIO =
            ZIO.fromAutoCloseable:
                ZIO.attemptBlockingIO(file.asCsvReader[T](rfc.withHeader.withCellSeparator(separator)))


        ZIO.scoped:
            for
                reader <- readerZIO
                (errorsCount, correctRecords) <- ZIO.attemptBlockingIO:
                    reader.foldLeft((0, List.empty[T]))(
                        (resultsTuple, row) => row match
                            case Right(value) => (resultsTuple._1, value :: resultsTuple._2)
                            case Left(error) => (resultsTuple._1 + 1, resultsTuple._2)
                    )
                _ <- ZIO.logInfo(s"The file ${file.getPath()} had ${correctRecords.size} correct rows and $errorsCount incorrect.")
            yield correctRecords
