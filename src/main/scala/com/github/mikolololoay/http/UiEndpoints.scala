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
import com.github.mikolololoay.http.Endpoints.EndpointsEnv


object UiEndpoints:
    val rootEndpoint: PublicEndpoint[Unit, Unit, String, Any] =
        endpoint.get
            .in("")
            .out(htmlBodyUtf8)
    val rootServerEndpoint: ZServerEndpoint[TableRepo[Movie], Any] =
        rootEndpoint.zServerLogic(_ =>
            TableRepo.getAll[Movie]
                .flatMap: movies =>
                    ZIO.succeed(HomePage.generate(MoviesView.fullBody(movies)).render)
                .catchAll(e => ZIO.fail(e.getMessage()))
        )

    val all: List[ZServerEndpoint[EndpointsEnv, Any]] =
        List(rootServerEndpoint.widen[EndpointsEnv])

