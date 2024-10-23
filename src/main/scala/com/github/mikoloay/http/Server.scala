package com.github.mikoloay.http

import com.github.mikoloay.models.Movie
import com.github.mikoloay.repositories.tablerepos.TableRepo
import sttp.tapir.PublicEndpoint
import sttp.tapir.generic.auto.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.server.ziohttp.ZioHttpServerOptions
import sttp.tapir.ztapir.*
import zio.*
import zio.http.Response
import zio.http.Routes
import zio.http.Server


/** Provides logic necessary to start a HTTP Server. */
object HttpServer:
    val port = 8081
    val serverOptions: ZioHttpServerOptions[Any] =
        ZioHttpServerOptions.customiseInterceptors.options

    val app: Routes[Endpoints.EndpointsEnv, Response] =
        ZioHttpInterpreter(serverOptions).toHttp(Endpoints.all)

    val start =
        for
            actualPort <- Server.install(app)
            _ <- Console.printLine(s"Started the Filmonator service.")
            _ <- Console.printLine(s"You can read API documentation here: http://localhost:${actualPort}/api/docs")
            _ <- Console.readLine
        yield ()
