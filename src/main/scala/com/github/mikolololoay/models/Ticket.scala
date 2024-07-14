package com.github.mikolololoay.models


import kantan.csv.RowDecoder


final case class Ticket(
    name: String,
    isDiscount: Boolean,
    description: String,
    priceInZloty: Int
)


object Ticket:
    given RowDecoder[Ticket] = RowDecoder.decoder(0, 1, 2, 3)(Ticket.apply)