package com.github.mikoloay.models

import kantan.csv.RowDecoder
import zio.json.{JsonEncoder, JsonDecoder}


final case class TicketTransaction(
    id: String,
    screeningId: String,
    ticketType: String
) derives JsonEncoder,
        JsonDecoder


object TicketTransaction:
    given RowDecoder[TicketTransaction] =
        RowDecoder.decoder(0, 1, 2)(TicketTransaction.apply)
