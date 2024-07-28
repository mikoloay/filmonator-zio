package com.github.mikolololoay.http

import sttp.tapir.PublicEndpoint
import sttp.tapir.ztapir.*
import sttp.tapir.generic.auto.*
import zio.*
import com.github.mikolololoay.repositories.TableRepo
import com.github.mikolololoay.models.Movie
import sttp.tapir.json.zio.*
import com.github.mikolololoay.models.Ticket
import zio.json.JsonEncoder
import zio.json.JsonDecoder
import sttp.tapir.Schema
import zio.http.Handler
import zio.http.template.Element.PartialElement
import com.github.mikolololoay.views.{MoviesView, HomePage}
import sttp.tapir.swagger.bundle.SwaggerInterpreter


object Endpoints:
    def crudEndpoints[A: JsonEncoder: JsonDecoder: Schema: Tag](endpointName: String) =
        val getAllEndpoint: PublicEndpoint[Unit, String, List[A], Any] =
            endpoint.get
                .in(endpointName)
                .out(jsonBody[List[A]])
                .errorOut(stringBody)
        val getAllServerEndpoint: ZServerEndpoint[TableRepo[A], Any] =
            getAllEndpoint
                .zServerLogic(_ => TableRepo.getAll[A].catchAll(e => ZIO.fail(e.getMessage())))

        val getByIdEndpoint: PublicEndpoint[String, String, List[A], Any] =
            endpoint.get
                .in(endpointName / path[String]("id"))
                .out(jsonBody[List[A]])
                .errorOut(stringBody)
        val getByIdServerEndpoint: ZServerEndpoint[TableRepo[A], Any] =
            getByIdEndpoint
                .zServerLogic(id => TableRepo.get[A](id).catchAll(e => ZIO.fail(e.getMessage())))

        val serverEndpoints = List(
            getAllServerEndpoint,
            getByIdServerEndpoint
        )
        serverEndpoints

    val rootEndpoint: PublicEndpoint[Unit, Unit, String, Any] =
        endpoint.get
            .in("")
            .out(htmlBodyUtf8)

    val rootServerEndpoint: ZServerEndpoint[TableRepo[Movie], Any] =
        rootEndpoint.zServerLogic(_ =>
            TableRepo
                .getAll[Movie]
                .flatMap: movies =>
                    ZIO.succeed(HomePage.generate(MoviesView.fullBody(movies)).render)
                .catchAll(e => ZIO.fail(e.getMessage()))
        )

    type EndpointsEnv = TableRepo[Movie]

    val all: List[ZServerEndpoint[EndpointsEnv, Any]] =
        val allCrudEndpoints = crudEndpoints[Movie]("movies")
        val serverEndpoints = rootServerEndpoint.widen[EndpointsEnv] :: allCrudEndpoints.map(_.widen[EndpointsEnv])
        val swaggerEndpoints = SwaggerInterpreter()
            .fromServerEndpoints(serverEndpoints, "Filmonator", "1.0")
        serverEndpoints ++ swaggerEndpoints
