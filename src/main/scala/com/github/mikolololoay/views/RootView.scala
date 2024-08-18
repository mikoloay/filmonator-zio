package com.github.mikolololoay.views

import com.github.mikolololoay.models.Movie
import com.github.mikolololoay.views.htmx.HtmxAttributes
import scalatags.Text
import scalatags.Text.all.*
import com.github.mikolololoay.models.aggregations.ScreeningWithDetails


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
            ),
            div(
                style := "display: flex; justify-content: space-between",
                button(
                    style := "width: 160px",
                    HtmxAttributes.get(s"/movies"),
                    HtmxAttributes.target("closest tr"),
                    HtmxAttributes.swap("outerHTML"),
                    HtmxAttributes.select("tbody > tr"),
                    "Load More"
                )
            )
        )
    )
