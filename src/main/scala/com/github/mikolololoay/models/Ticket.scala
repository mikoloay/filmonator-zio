package com.github.mikoloay.models

import kantan.csv.RowDecoder
import zio.json.{JsonEncoder, JsonDecoder}


final case class Ticket(
    name: String,
    isDiscount: Boolean,
    description: String,
    priceInZloty: Int
) derives JsonEncoder,
        JsonDecoder


object Ticket:
    given RowDecoder[Ticket] = RowDecoder.decoder(0, 1, 2, 3)(Ticket.apply)
