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
        val getAll: ZServerEndpoint[TableRepo[A], Any] =
            baseApiEndpoint.get
                .in(endpointName)
                .out(jsonBody[List[A]])
                .zServerLogic(_ => TableRepo.getAll[A].catchAll(e => ZIO.fail(e.getMessage())))

        val getById: ZServerEndpoint[TableRepo[A], Any] =
            baseApiEndpoint.get
                .in(endpointName / path[String]("id"))
                .out(jsonBody[List[A]])
                .zServerLogic(id => TableRepo.get[A](id).catchAll(e => ZIO.fail(e.getMessage())))

        val addMultiple: ZServerEndpoint[TableRepo[A], Any] =
            baseApiEndpoint.post
                .in(endpointName)
                .in(jsonBody[List[A]])
                .out(stringBody)
                .zServerLogic(items =>
                    (
                        TableRepo.add[A](items) *>
                        ZIO.succeed(s"Successfully added ${items.size} new $endpointName.")
                    )
                    .catchAll(e => ZIO.fail(e.getMessage()))
                )
    
        val upsert: ZServerEndpoint[TableRepo[A], Any] =
            baseApiEndpoint.put
                .in(endpointName)
                .in(jsonBody[A])
                .out(stringBody)
                .zServerLogic(item =>
                    (
                        TableRepo.upsert[A](item) *>
                        ZIO.succeed(s"Successfully upserted item.")
                    )
                    .catchAll(e => ZIO.fail(e.getMessage()))
                )
        
        val delete: ZServerEndpoint[TableRepo[A], Any] =
            baseApiEndpoint.delete
                .in(endpointName / path[String]("id"))
                .out(stringBody)
                .zServerLogic(id =>
                    (
                        TableRepo.delete[A](id) *>
                        ZIO.succeed(s"Successfully deleted item.")
                    )
                    .catchAll(e => ZIO.fail(e.getMessage()))
                )

        val serverEndpoints = List(
            getAll,
            getById,
            addMultiple,
            upsert,
            delete
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
