package com.github.mikoloay.views

import com.github.mikoloay.models.Movie
import com.github.mikoloay.views.htmx.HtmxAttributes
import scalatags.Text
import scalatags.Text.all.*
import com.github.mikoloay.models.Screening


object ScreeningsView:
    def fullBody(
            screenings: List[Screening] = List.empty,
    ): Text.TypedTag[String] =
        div(listView(screenings))

    def listView[A](screenings: List[Screening]) = div(
        `class` := "container",
        form(
            table(
                `class` := "table",
                thead(
                    tr(
                        th(),
                        th("screeningId"),
                        th("roomId"),
                        th("movieId"),
                        th("date"),
                    )
                ),
                tbody(
                    screenings.map(screening =>
                        tr(
                            td(input(`type` := "checkbox", name := "selected_screenings_ids", value := screening.screeningId)),
                            td(screening.screeningId),
                            td(screening.roomId),
                            td(screening.movieId),
                            td(screening.date),
                            td(a(href := s"/api/screenings/${screening.screeningId}", "View"))
                        )
                    )
                )
            )
        )
    )
