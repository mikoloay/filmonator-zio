package com.github.mikoloay.views

import com.github.mikoloay.models.Movie
import com.github.mikoloay.views.htmx.HtmxAttributes
import scalatags.Text
import scalatags.Text.all.*
import com.github.mikoloay.models.Ticket


object TicketsView:
    def fullBody(
            tickets: List[Ticket] = List.empty,
    ): Text.TypedTag[String] =
        div(listView(tickets))

    def listView[A](tickets: List[Ticket]) = div(
        `class` := "container",
        form(
            table(
                `class` := "table",
                thead(
                    tr(
                        th(),
                        th("name"),
                        th("isDiscount"),
                        th("description"),
                        th("priceInZloty")
                    )
                ),
                tbody(
                    tickets.map(ticket =>
                        tr(
                            td(input(`type` := "checkbox", name := "selected_tickets_ids", value := ticket.name)),
                            td(ticket.name),
                            td(ticket.isDiscount.toString()),
                            td(ticket.description),
                            td(ticket.priceInZloty),
                            td(a(href := s"/api/tickets/${ticket.name}", "View"))
                        )
                    )
                )
            )
        )
    )
