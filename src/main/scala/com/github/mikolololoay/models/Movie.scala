package com.github.mikoloay.models

import kantan.csv.RowDecoder
import zio.json.{JsonEncoder, JsonDecoder}


final case class Movie(
    id: String,
    name: String,
    yearOfProduction: Int,
    director: String,
    description: String,
    lengthInMinutes: Int
) derives JsonEncoder,
        JsonDecoder


object Movie:
    given RowDecoder[Movie] = RowDecoder.decoder(0, 1, 2, 3, 4, 5)(Movie.apply)
