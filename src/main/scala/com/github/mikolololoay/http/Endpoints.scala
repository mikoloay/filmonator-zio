package com.github.mikolololoay.http

import com.github.mikolololoay.repositories.tablerepos.TableRepo
import com.github.mikolololoay.models.{Movie, Screening, ScreeningRoom, Ticket, TicketTransaction}
import com.github.mikolololoay.repositories.AggregationsRepo

object Endpoints:
    type EndpointsEnv = TableRepo[Movie] & TableRepo[Screening] & TableRepo[ScreeningRoom] & TableRepo[Ticket] &
        TableRepo[TicketTransaction] & AggregationsRepo
    
    val all =
        ApiEndpoints.all
        ++ UiEndpoints.all