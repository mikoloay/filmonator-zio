package com.github.mikoloay.models.aggregations

import zio.json.{JsonEncoder, JsonDecoder}

final case class ScreeningWithDetails(
    date: String,
    roomName: String,
    movieName: String,
    ticketsSold: Int,
    revenue: Int
) derives JsonEncoder, JsonDecoder
