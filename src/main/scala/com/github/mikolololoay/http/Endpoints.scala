package com.github.mikolololoay.http

import com.github.mikolololoay.models.Movie
import com.github.mikolololoay.models.Screening
import com.github.mikolololoay.models.ScreeningRoom
import com.github.mikolololoay.models.Ticket
import com.github.mikolololoay.models.TicketTransaction
import com.github.mikolololoay.repositories.AggregationsRepo
import com.github.mikolololoay.repositories.SessionRepo
import com.github.mikolololoay.repositories.tablerepos.TableRepo


/** Gathers all endpoints from other objects. */
object Endpoints:
    type EndpointsEnv = TableRepo[Movie] & TableRepo[Screening] & TableRepo[ScreeningRoom] & TableRepo[Ticket] &
        TableRepo[TicketTransaction] & AggregationsRepo & SessionRepo

    val all =
        ApiEndpoints.all
            ++ UiEndpoints.all
            ++ AuthEndpoints.all
