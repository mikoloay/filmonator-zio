package com.github.mikoloay.utils


import zio.*
import zio.test.*
import zio.test.Assertion.*
import com.github.mikoloay.models.Movie
import java.io.File


object CsvReaderSpec extends ZIOSpecDefault:
    val spec = suite("CsvReaderSpec")(
        test("read a csv file"):
            val path = "src/test/resources/test_csvs/movies.csv"
            val separator = '|'
            for
                records <- CsvReader.readFromFile[Movie](File(path), separator)
            yield (
                assertTrue(records.size == 3)
                && assertTrue(records.filter(_.isInstanceOf[Movie]).size == 3)
            )
    )