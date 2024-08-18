package com.github.mikolololoay.http


import sttp.tapir.PublicEndpoint
import sttp.tapir.ztapir.*
import sttp.tapir.generic.auto.*
import zio.*
import zio.http.{Routes, Response, Server}
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.server.ziohttp.ZioHttpServerOptions
import com.github.mikolololoay.repositories.TableRepo
import com.github.mikolololoay.models.Movie


object HttpServer:
    val port = 8081
    val serverOptions: ZioHttpServerOptions[Any] =
        ZioHttpServerOptions.customiseInterceptors.options
    val app: Routes[Endpoints.EndpointsEnv, Response] =
        ZioHttpInterpreter(serverOptions).toHttp(Endpoints.all)
    
    val start =
        for
            actualPort <- Server.install(app)
            _ <- Console.printLine(s"HEHE http://localhost:${actualPort}/docs")
            _ <- Console.readLine
        yield ()
