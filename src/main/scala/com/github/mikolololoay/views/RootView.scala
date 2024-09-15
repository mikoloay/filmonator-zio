package com.github.mikoloay.views

import com.github.mikoloay.models.Movie
import com.github.mikoloay.views.htmx.HtmxAttributes
import scalatags.Text
import scalatags.Text.all.*
import com.github.mikoloay.models.aggregations.ScreeningWithDetails


object RootView:
    def fullBody(
            screeningsWithDetails: List[ScreeningWithDetails] = List.empty,
    ): Text.TypedTag[String] =
        div(listView(screeningsWithDetails))

    def listView(screeningsWithDetails: List[ScreeningWithDetails]) = div(
        `class` := "container",
        form(
            table(
                `class` := "table",
                thead(
                    tr(
                        th(),
                        th("date"),
                        th("roomName"),
                        th("movieName"),
                        th("ticketsSold"),
                        th("revenue"),
                    )
                ),
                tbody(
                    screeningsWithDetails.map: row =>
                        tr(
                            td(input(`type` := "checkbox", name := "selected_movies_ids", value := row.date)),
                            td(row.date),
                            td(row.roomName),
                            td(row.movieName),
                            td(row.ticketsSold),
                            td(row.revenue)
                        )
                )
            )
        )
    )
