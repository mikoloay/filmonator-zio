package com.github.mikolololoay.utils

import kantan.csv.*
import kantan.csv.ops.*
import kantan.csv.generic.*
import java.io.File
import zio.*
import kantan.codecs.resource.ResourceIterator
import scala.io.Codec


object CsvReader:
    def readFromFile[T: HeaderDecoder](file: File, separator: Char) =
        given Codec = Codec.UTF8

        val readerZIO =
            ZIO.fromAutoCloseable:
                ZIO.attemptBlockingIO(
                    file.asCsvReader[T](
                        rfc.withHeader.withCellSeparator(separator)
                    )
                )

        ZIO.scoped:
            for
                reader <- readerZIO
                (errorsCount, correctRecords) <- ZIO.attemptBlockingIO:
                    reader.foldLeft((0, List.empty[T])) { case ((errorsCount, correctRecords), row) =>
                        row match
                            case Right(value) =>
                                (errorsCount, value :: correctRecords)
                            case Left(error) =>
                                (errorsCount + 1, correctRecords)
                    }
                _ <- ZIO.logInfo(
                    s"The file ${file.getPath()} had ${correctRecords.size} correct rows and $errorsCount incorrect."
                )
            yield correctRecords
