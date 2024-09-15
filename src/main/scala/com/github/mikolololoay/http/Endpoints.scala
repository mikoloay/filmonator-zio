package com.github.mikoloay.http

import com.github.mikoloay.models.Movie
import com.github.mikoloay.models.Screening
import com.github.mikoloay.models.ScreeningRoom
import com.github.mikoloay.models.Ticket
import com.github.mikoloay.models.TicketTransaction
import com.github.mikoloay.repositories.AggregationsRepo
import com.github.mikoloay.repositories.SessionRepo
import com.github.mikoloay.repositories.tablerepos.TableRepo


/** Gathers all endpoints from other objects. */
object Endpoints:
    type EndpointsEnv = TableRepo[Movie] & TableRepo[Screening] & TableRepo[ScreeningRoom] & TableRepo[Ticket] &
        TableRepo[TicketTransaction] & AggregationsRepo & SessionRepo

    val all =
        ApiEndpoints.all
            ++ UiEndpoints.all
            ++ AuthEndpoints.all
