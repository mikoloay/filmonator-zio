package com.github.mikolololoay.views

import com.github.mikolololoay.models.Movie
import com.github.mikolololoay.views.htmx.HtmxAttributes
import scalatags.Text
import scalatags.Text.all.*
import com.github.mikolololoay.models.TicketTransaction


object TicketTransactionsView:
    def fullBody(
            transactions: List[TicketTransaction] = List.empty,
    ): Text.TypedTag[String] =
        div(listView(transactions))

    def listView[A](transactions: List[TicketTransaction]) = div(
        `class` := "container",
        form(
            table(
                `class` := "table",
                thead(
                    tr(
                        th(),
                        th("id"),
                        th("screeningId"),
                        th("ticketType")
                    )
                ),
                tbody(
                    transactions.map(transaction =>
                        tr(
                            td(input(`type` := "checkbox", name := "selected_movies_ids", value := transaction.id)),
                            td(transaction.id),
                            td(transaction.screeningId),
                            td(transaction.ticketType),
                            td(a(href := s"/api/transactions/${transaction.id}", "View"))
                        )
                    )
                )
            )
        )
    )
