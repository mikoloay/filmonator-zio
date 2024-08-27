package com.github.mikolololoay.utils

import kantan.csv.*
import kantan.csv.ops.*
import kantan.csv.generic.*
import java.io.File
import zio.*
import kantan.codecs.resource.ResourceIterator
import scala.io.Codec


/** Provides utilities for interacting with csv files */
object CsvReader:
    /** Returns a list of instances of a selected type parsed from a csv file.
      * 
      * It's a simplified csv file reader which leaves most csv properties
      * with their default values.
      * Faulty rows which can't be converted to the selected type will be skipped.
      */
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
