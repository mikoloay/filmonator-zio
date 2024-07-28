package com.github.mikolololoay.models

import kantan.csv.RowDecoder
import zio.json.{JsonEncoder, JsonDecoder}


final case class Transaction(
    id: String,
    screeningId: String,
    ticketType: String
) derives JsonEncoder,
        JsonDecoder


object Transaction:
    given RowDecoder[Transaction] =
        RowDecoder.decoder(0, 1, 2)(Transaction.apply)
