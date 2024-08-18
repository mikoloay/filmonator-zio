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
import sttp.tapir.docs.openapi.OpenAPIDocsOptions
import sttp.tapir.swagger.SwaggerUIOptions


object ApiEndpoints:
    val baseApiPrefix = "api"
    val baseApiEndpoint = endpoint
        .in(baseApiPrefix)
        .errorOut(stringBody)

    def crudEndpoints[A: JsonEncoder: JsonDecoder: Schema: Tag](endpointName: String) =
        val getAllEndpoint: PublicEndpoint[Unit, String, List[A], Any] =
            baseApiEndpoint.get
                .in(endpointName)
                .out(jsonBody[List[A]])
        val getAllServerEndpoint: ZServerEndpoint[TableRepo[A], Any] =
            getAllEndpoint
                .zServerLogic(_ => TableRepo.getAll[A].catchAll(e => ZIO.fail(e.getMessage())))

        val getByIdEndpoint: PublicEndpoint[String, String, List[A], Any] =
            baseApiEndpoint.get
                .in(endpointName / path[String]("id"))
                .out(jsonBody[List[A]])
        val getByIdServerEndpoint: ZServerEndpoint[TableRepo[A], Any] =
            getByIdEndpoint
                .zServerLogic(id => TableRepo.get[A](id).catchAll(e => ZIO.fail(e.getMessage())))

        val serverEndpoints = List(
            getAllServerEndpoint,
            getByIdServerEndpoint
        )

        serverEndpoints

    val all: List[ZServerEndpoint[EndpointsEnv, Any]] =
        val allCrudEndpoints =
            crudEndpoints[Movie]("movies").map(_.widen[EndpointsEnv])
                ++ crudEndpoints[Screening]("screenings").map(_.widen[EndpointsEnv])
                ++ crudEndpoints[ScreeningRoom]("screening_rooms").map(_.widen[EndpointsEnv])
                ++ crudEndpoints[Ticket]("tickets").map(_.widen[EndpointsEnv])
                ++ crudEndpoints[TicketTransaction]("transactions").map(_.widen[EndpointsEnv])

        val swaggerEndpoints = SwaggerInterpreter(swaggerUIOptions =
            SwaggerUIOptions.default.copy(pathPrefix = List(baseApiPrefix, "docs"))
        )
            .fromServerEndpoints(allCrudEndpoints, "Filmonator", "1.0")

        allCrudEndpoints ++ swaggerEndpoints
