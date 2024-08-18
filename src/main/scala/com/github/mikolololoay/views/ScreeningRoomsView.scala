package com.github.mikolololoay.views

import com.github.mikolololoay.models.Movie
import com.github.mikolololoay.views.htmx.HtmxAttributes
import scalatags.Text
import scalatags.Text.all.*
import com.github.mikolololoay.models.ScreeningRoom


object ScreeningRoomsView:
    def fullBody(
            screeningRooms: List[ScreeningRoom] = List.empty,
    ): Text.TypedTag[String] =
        div(listView(screeningRooms))

    def listView[A](screeningRooms: List[ScreeningRoom]) = div(
        `class` := "container",
        form(
            table(
                `class` := "table",
                thead(
                    tr(
                        th(),
                        th("id"),
                        th("roomName"),
                        th("capacity"),
                        th("has3d"),
                        th("screenType"),
                        th("audioSystem")
                    )
                ),
                tbody(
                    screeningRooms.map(room =>
                        tr(
                            td(input(`type` := "checkbox", name := "selected_screening_rooms_ids", value := room.id)),
                            td(room.id),
                            td(room.roomName),
                            td(room.capacity),
                            td(room.has3d.toString()),
                            td(room.screenType),
                            td(room.audioSystem),
                            td(a(href := s"/api/screening_rooms/${room.id}", "View"))
                        )
                    )
                )
            )
        )
    )
