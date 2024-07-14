package com.github.mikolololoay.models


import kantan.csv.RowDecoder


final case class Movie(
    id: String,
    name: String,
    yearOfProduction: Int,
    director: String,
    description: String,
    lengthInMinutes: Int
)

object Movie:
    given RowDecoder[Movie] = RowDecoder.decoder(0, 1, 2, 3, 4, 5)(Movie.apply)