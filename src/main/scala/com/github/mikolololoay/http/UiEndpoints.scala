package com.github.mikoloay.http

import com.github.mikoloay.http.Endpoints.EndpointsEnv
import com.github.mikoloay.models.Movie
import com.github.mikoloay.models.Screening
import com.github.mikoloay.models.ScreeningRoom
import com.github.mikoloay.models.Ticket
import com.github.mikoloay.models.TicketTransaction
import com.github.mikoloay.repositories.AggregationsRepo
import com.github.mikoloay.repositories.SessionRepo
import com.github.mikoloay.repositories.tablerepos.TableRepo
import com.github.mikoloay.utils.DateUtils.Date
import com.github.mikoloay.views.LoginView
import com.github.mikoloay.views.MoviesView
import com.github.mikoloay.views.PageGenerator
import com.github.mikoloay.views.RootView
import com.github.mikoloay.views.ScreeningRoomsView
import com.github.mikoloay.views.ScreeningsView
import com.github.mikoloay.views.TicketTransactionsView
import com.github.mikoloay.views.TicketsView
import sttp.tapir.PublicEndpoint
import sttp.tapir.Schema
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.*
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.*
import zio.*
import zio.http.Handler
import zio.http.template.Element.PartialElement
import zio.json.JsonDecoder
import zio.json.JsonEncoder


/** Provides all endpoints related to UI. */
object UiEndpoints:
    val rootEndpoint: ZServerEndpoint[AggregationsRepo & SessionRepo, Any] =
        AuthEndpoints.secureEndpoint.get
            .in("")
            .out(htmlBodyUtf8)
            .serverLogic(authIn =>
                _ => {
                    (for
                        date <- ZIO.fromOption(Date("2024-03-04"))
                        screeningsWithDetails <- AggregationsRepo.getScreeningsWithDetailsForDate(date)
                    yield PageGenerator.generate(RootView.fullBody(screeningsWithDetails)).render)
                        .catchAll:
                            case e: Exception => ZIO.succeed(e.getMessage())
                            case _ =>
                                ZIO.succeed(
                                    "The value you provided couldn't be converted to a date!"
                                ) // TODO: improve this section (error handling could be better here)
                }
            )

    val moviesEndpoint: ZServerEndpoint[TableRepo[Movie] & SessionRepo, Any] =
        AuthEndpoints.secureEndpoint.get
            .in("movies")
            .out(htmlBodyUtf8)
            .serverLogic(authIn =>
                _ =>
                    (
                        for movies <- TableRepo.getAll[Movie]
                        yield PageGenerator.generate(MoviesView.listView(movies)).render
                    )
                        .catchAll(e => ZIO.succeed(e.getMessage()))
            )

    val screeningsEndpoint: ZServerEndpoint[TableRepo[Screening] & SessionRepo, Any] =
        AuthEndpoints.secureEndpoint.get
            .in("screenings")
            .out(htmlBodyUtf8)
            .serverLogic(authIn =>
                hxHeader =>
                    (
                        for screenings <- TableRepo.getAll[Screening]
                        yield PageGenerator.generate(ScreeningsView.listView(screenings)).render
                    )
                        .catchAll(e => ZIO.succeed(e.getMessage()))
            )

    val screeningRoomsEndpoint: ZServerEndpoint[TableRepo[ScreeningRoom] & SessionRepo, Any] =
        AuthEndpoints.secureEndpoint.get
            .in("screening_rooms")
            .out(htmlBodyUtf8)
            .serverLogic(authIn =>
                hxHeader =>
                    (
                        for rooms <- TableRepo.getAll[ScreeningRoom]
                        yield PageGenerator.generate(ScreeningRoomsView.listView(rooms)).render
                    )
                        .catchAll(e => ZIO.succeed(e.getMessage()))
            )

    val ticketsEndpoint: ZServerEndpoint[TableRepo[Ticket] & SessionRepo, Any] =
        AuthEndpoints.secureEndpoint.get
            .in("tickets")
            .out(htmlBodyUtf8)
            .serverLogic(authIn =>
                hxHeader =>
                    (
                        for tickets <- TableRepo.getAll[Ticket]
                        yield PageGenerator.generate(TicketsView.listView(tickets)).render
                    )
                        .catchAll(e => ZIO.succeed(e.getMessage()))
            )

    val transactionsEndpoint: ZServerEndpoint[TableRepo[TicketTransaction] & SessionRepo, Any] =
        AuthEndpoints.secureEndpoint.get
            .in("transactions")
            .out(htmlBodyUtf8)
            .serverLogic(authIn =>
                hxHeader =>
                    (
                        for transactions <- TableRepo.getAll[TicketTransaction]
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
