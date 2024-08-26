package com.github.mikolololoay.http

import sttp.tapir.PublicEndpoint
import sttp.tapir.ztapir.*
import sttp.tapir.generic.auto.*
import zio.*
import com.github.mikolololoay.repositories.tablerepos.TableRepo
import com.github.mikolololoay.models.{Movie, Screening, ScreeningRoom, Ticket, TicketTransaction}
import sttp.tapir.json.zio.*
import zio.json.JsonEncoder
import zio.json.JsonDecoder
import sttp.tapir.Schema
import zio.http.Handler
import zio.http.template.Element.PartialElement
import com.github.mikolololoay.views.{MoviesView, PageGenerator}
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import com.github.mikolololoay.http.Endpoints.EndpointsEnv
import com.github.mikolololoay.repositories.{AggregationsRepo, SessionRepo}
import com.github.mikolololoay.utils.DateUtils.Date
import com.github.mikolololoay.views.{RootView, MoviesView, ScreeningRoomsView, ScreeningsView, TicketsView, TicketTransactionsView}
import com.github.mikolololoay.views.LoginView


object UiEndpoints:
    val rootEndpoint: ZServerEndpoint[AggregationsRepo & SessionRepo, Any] =
        AuthEndpoints.secureEndpoint.get
            .in("")
            .out(htmlBodyUtf8)
            .serverLogic(authIn => _ => {
                (for
                    date <- ZIO.fromOption(Date("2024-03-04"))
                    screeningsWithDetails <- AggregationsRepo.getScreeningsWithDetailsForDate(date)
                yield PageGenerator.generate(RootView.fullBody(screeningsWithDetails)).render)
                .catchAll:
                    case e: Exception => ZIO.succeed(e.getMessage())
                    case _ => ZIO.succeed("The value you provided couldn't be converted to a date!")  // TODO: do it better
            }
            )


    val moviesEndpoint: ZServerEndpoint[TableRepo[Movie] & SessionRepo, Any] =
        AuthEndpoints.secureEndpoint.get
            .in("movies")
            .out(htmlBodyUtf8)
            .serverLogic(authIn => _ =>
                (
                    for
                        movies <- TableRepo.getAll[Movie]
                    yield PageGenerator.generate(MoviesView.listView(movies)).render
                )
                .catchAll(e => ZIO.succeed(e.getMessage()))
        )

    val screeningsEndpoint: ZServerEndpoint[TableRepo[Screening] & SessionRepo, Any] =
        AuthEndpoints.secureEndpoint.get
            .in("screenings")
            .out(htmlBodyUtf8)
            .serverLogic(authIn => hxHeader =>
                (
                    for
                        screenings <- TableRepo.getAll[Screening]
                    yield PageGenerator.generate(ScreeningsView.listView(screenings)).render
                )
                .catchAll(e => ZIO.succeed(e.getMessage()))
        )

    val screeningRoomsEndpoint: ZServerEndpoint[TableRepo[ScreeningRoom] & SessionRepo, Any] =
        AuthEndpoints.secureEndpoint.get
            .in("screening_rooms")
            .out(htmlBodyUtf8)
            .serverLogic(authIn => hxHeader =>
                (
                    for
                        rooms <- TableRepo.getAll[ScreeningRoom]
                    yield PageGenerator.generate(ScreeningRoomsView.listView(rooms)).render
                )
                .catchAll(e => ZIO.succeed(e.getMessage()))
        )

    val ticketsEndpoint: ZServerEndpoint[TableRepo[Ticket] & SessionRepo, Any] =
        AuthEndpoints.secureEndpoint.get
            .in("tickets")
            .out(htmlBodyUtf8)
            .serverLogic(authIn => hxHeader =>
                (
                    for
                        tickets <- TableRepo.getAll[Ticket]
                    yield PageGenerator.generate(TicketsView.listView(tickets)).render
                )
                .catchAll(e => ZIO.succeed(e.getMessage()))
        )

    val transactionsEndpoint: ZServerEndpoint[TableRepo[TicketTransaction] & SessionRepo, Any] =
        AuthEndpoints.secureEndpoint.get
            .in("transactions")
            .out(htmlBodyUtf8)
            .serverLogic(authIn => hxHeader =>
                (
                    for
                        transactions <- TableRepo.getAll[TicketTransaction]
                    yield PageGenerator.generate(TicketTransactionsView.listView(transactions)).render
                )
                .catchAll(e => ZIO.succeed(e.getMessage()))
        )

    val all: List[ZServerEndpoint[EndpointsEnv, Any]] =
        List(
            rootEndpoint.widen[EndpointsEnv],
            moviesEndpoint.widen[EndpointsEnv],
            screeningsEndpoint.widen[EndpointsEnv],
            screeningRoomsEndpoint.widen[EndpointsEnv],
            ticketsEndpoint.widen[EndpointsEnv],
            transactionsEndpoint.widen[EndpointsEnv]
        )

