package com.github.mikolololoay.models


import kantan.csv.RowDecoder


final case class Screening(
    screeningId: String,
    roomId: String,
    movieId: String,
    date: String
)


object Screening:
    given RowDecoder[Screening] = RowDecoder.decoder(0, 1, 2, 3)(Screening.apply)