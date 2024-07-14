package com.github.mikolololoay.models


import kantan.csv.RowDecoder


final case class Transaction(
    id: String,
    screeningId: String,
    ticketType: String
)


object Transaction:
    given RowDecoder[Transaction] = RowDecoder.decoder(0, 1, 2)(Transaction.apply)