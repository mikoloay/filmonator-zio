package com.github.mikolololoay.views

import com.github.mikolololoay.models.Movie
import com.github.mikolololoay.views.htmx.HtmxAttributes
import scalatags.Text
import scalatags.Text.all.*


object MoviesView:
    def fullBody(
            movies: List[Movie] = List.empty,
    ): Text.TypedTag[String] =
        div(listView(movies))

    private def searchForm(formInputValue: String): Text.TypedTag[String] = form(
        action := "/movies",
        method := "get",
        `class` := "tool-bar",
        h1(
            `for` := "search",
            "Filmonator - a Scala + HTMX demo"
        ),
        div(
            style := "display: flex",
            input(
                id := "search-input",
                `type` := "search",
                style := "flex: 1; margin-right: 20px",
                name := "q",
                value := formInputValue,
                HtmxAttributes.get("/movies"),
                HtmxAttributes.trigger("search, keyup delay:200ms changed"),
                HtmxAttributes.target("tbody"),
                HtmxAttributes.pushUrl(),
                HtmxAttributes.indicator(),
                HtmxAttributes.select("tbody tr")
            ),
            input(
                `type` := "submit",
                id := "search-submit",
                style := "flex: 0 0 100px",
                value := "Search"
            )
        )
    )

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
                            td(a(href := s"/movies/${movie.id}", "View"))
                        )
                    )
                )
            ),
            div(
                style := "display: flex; justify-content: space-between",
                button(
                    style := "width: 160px",
                    HtmxAttributes.get(s"/movies"), // HtmxAttributes.get(s"/movies?page=${page}&q=${searchTerm}"),
                    HtmxAttributes.target("closest tr"),
                    HtmxAttributes.swap("outerHTML"),
                    HtmxAttributes.select("tbody > tr"),
                    "Load More"
                )
            )
        )
    )
