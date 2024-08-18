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
import com.github.mikolololoay.repositories.AggregationsRepo
import com.github.mikolololoay.utils.DateUtils.Date
import com.github.mikolololoay.views.{RootView, MoviesView, ScreeningRoomsView, ScreeningsView, TicketsView, TicketTransactionsView}


object UiEndpoints:
    val rootEndpoint: PublicEndpoint[Unit, Unit, String, Any] =
        endpoint.get
            .in("")
            .out(htmlBodyUtf8)
    val rootServerEndpoint: ZServerEndpoint[AggregationsRepo, Any] =
        rootEndpoint.zServerLogic(_ =>
            (for
                date <- ZIO.fromOption(Date("2024-03-04"))
                screeningsWithDetails <- AggregationsRepo.getScreeningsWithDetailsForDate(date)
            yield PageGenerator.generate(RootView.fullBody(screeningsWithDetails)).render)
            .catchAll:
                case e: Exception => ZIO.fail(e.getMessage())
                case _ => ZIO.fail("The value you provided couldn't be converted to a date!")
        )


    val moviesEndpoint: ZServerEndpoint[TableRepo[Movie], Any] =
        endpoint.get
            .in("movies")
            .in(header[Option[String]]("HX-Trigger"))
            .out(htmlBodyUtf8)
            .zServerLogic(hxHeader =>
                (
                    for
                        _ <- Console.printLine(hxHeader.getOrElse("NI MA"))
                        movies <- TableRepo.getAll[Movie]
                    yield PageGenerator.generate(MoviesView.listView(movies)).render
                )
                .catchAll(e => ZIO.fail(e.getMessage()))
        )

    val screeningsEndpoint: ZServerEndpoint[TableRepo[Screening], Any] =
        endpoint.get
            .in("screenings")
            .in(header[Option[String]]("HX-Trigger"))
            .out(htmlBodyUtf8)
            .zServerLogic(hxHeader =>
                (
                    for
                        _ <- Console.printLine(hxHeader.getOrElse("NI MA"))
                        screenings <- TableRepo.getAll[Screening]
                    yield PageGenerator.generate(ScreeningsView.listView(screenings)).render
                )
                .catchAll(e => ZIO.fail(e.getMessage()))
        )

    val screeningRoomsEndpoint: ZServerEndpoint[TableRepo[ScreeningRoom], Any] =
        endpoint.get
            .in("screening_rooms")
            .in(header[Option[String]]("HX-Trigger"))
            .out(htmlBodyUtf8)
            .zServerLogic(hxHeader =>
                (
                    for
                        _ <- Console.printLine(hxHeader.getOrElse("NI MA"))
                        rooms <- TableRepo.getAll[ScreeningRoom]
                    yield PageGenerator.generate(ScreeningRoomsView.listView(rooms)).render
                )
                .catchAll(e => ZIO.fail(e.getMessage()))
        )

    val ticketsEndpoint: ZServerEndpoint[TableRepo[Ticket], Any] =
        endpoint.get
            .in("tickets")
            .in(header[Option[String]]("HX-Trigger"))
            .out(htmlBodyUtf8)
            .zServerLogic(hxHeader =>
                (
                    for
                        _ <- Console.printLine(hxHeader.getOrElse("NI MA"))
                        tickets <- TableRepo.getAll[Ticket]
                    yield PageGenerator.generate(TicketsView.listView(tickets)).render
                )
                .catchAll(e => ZIO.fail(e.getMessage()))
        )

    val transactionsEndpoint: ZServerEndpoint[TableRepo[TicketTransaction], Any] =
        endpoint.get
            .in("transactions")
            .in(header[Option[String]]("HX-Trigger"))
            .out(htmlBodyUtf8)
            .zServerLogic(hxHeader =>
                (
                    for
                        _ <- Console.printLine(hxHeader.getOrElse("NI MA"))
                        transactions <- TableRepo.getAll[TicketTransaction]
                    yield PageGenerator.generate(TicketTransactionsView.listView(transactions)).render
                )
                .catchAll(e => ZIO.fail(e.getMessage()))
        )

    val all: List[ZServerEndpoint[EndpointsEnv, Any]] =
        List(
            rootServerEndpoint.widen[EndpointsEnv],
            moviesEndpoint.widen[EndpointsEnv],
            screeningsEndpoint.widen[EndpointsEnv],
            screeningRoomsEndpoint.widen[EndpointsEnv],
            ticketsEndpoint.widen[EndpointsEnv],
            transactionsEndpoint.widen[EndpointsEnv]
        )

