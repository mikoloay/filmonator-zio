package com.github.mikolololoay.http

import com.github.mikolololoay.repositories.TableRepo
import com.github.mikolololoay.models.{Movie, Screening, ScreeningRoom, Ticket, TicketTransaction}

object Endpoints:
    type EndpointsEnv = TableRepo[Movie] & TableRepo[Screening] & TableRepo[ScreeningRoom] & TableRepo[Ticket] &
        TableRepo[TicketTransaction]
    
    val all =
        ApiEndpoints.all
        ++ UiEndpoints.all