package com.github.mikoloay.models

import kantan.csv.RowDecoder
import zio.json.{JsonEncoder, JsonDecoder}


final case class Screening(
    screeningId: String,
    roomId: String,
    movieId: String,
    date: String
) derives JsonEncoder,
        JsonDecoder


object Screening:
    given RowDecoder[Screening] =
        RowDecoder.decoder(0, 1, 2, 3)(Screening.apply)
