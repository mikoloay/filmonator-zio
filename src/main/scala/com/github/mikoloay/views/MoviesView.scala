package com.github.mikoloay.views

import com.github.mikoloay.models.Movie
import com.github.mikoloay.views.htmx.HtmxAttributes
import scalatags.Text
import scalatags.Text.all.*


object MoviesView:
    def fullBody(
            movies: List[Movie] = List.empty,
    ): Text.TypedTag[String] =
        div(listView(movies))

    def listView(movies: List[Movie]) = div(
        `class` := "container",
        form(
            table(
                `class` := "table",
                thead(
                    tr(
                        th(),
                        th("id"),
                        th("name"),
                        th("yearOfProduction"),
                        th("director"),
                        th("description"),
                        th("lengthInMinutes")
                    )
                ),
                tbody(
                    movies.map(movie =>
                        val shortDescription =
                            if movie.description.length <= 40 then
                                movie.description
                            else
                                movie.description.take(37) + "..."

                        tr(
                            td(input(`type` := "checkbox", name := "selected_movies_ids", value := movie.id)),
                            td(movie.id),
                            td(movie.name),
                            td(movie.yearOfProduction),
                            td(movie.director),
                            td(shortDescription),
                            td(movie.lengthInMinutes),
                            td(a(href := s"/api/movies/${movie.id}", "View"))
                        )
                    )
                )
            )
        )
    )
