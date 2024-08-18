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


object Endpoints:
    def crudEndpoints[A : JsonEncoder : JsonDecoder : Schema : Tag](
        endpointName: String
    ): List[ZServerEndpoint[TableRepo[A], Any]] =
        val getAllEndpoint: ZServerEndpoint[TableRepo[A], Any] =
            endpoint
                .get
                .in(endpointName)
                .out(jsonBody[List[A]])
                .errorOut(stringBody)
                .zServerLogic(_ => TableRepo.getAll[A].catchAll(e => ZIO.fail(e.getMessage())))

        val getByIdEndpoint: ZServerEndpoint[TableRepo[A], Any] =
            endpoint
                .get
                .in(endpointName / query[String]("id"))
                .out(jsonBody[List[A]])
                .errorOut(stringBody)
                .zServerLogic(id => TableRepo.get[A](id).catchAll(e => ZIO.fail(e.getMessage())))
        
        List(
            getAllEndpoint,
            getByIdEndpoint
        )


    val helloEndpoint: PublicEndpoint[String, Unit, String, Any] =
        endpoint
            .get
            .in("hello" / query[String]("name"))
            .out(stringBody)
    val helloServerEndpoint: ZServerEndpoint[Any, Any] =
        helloEndpoint.zServerLogic(name => ZIO.succeed(s"Siemanko $name"))

    val getMoviesEndpoint: PublicEndpoint[Unit, String, List[Movie], Any] =
        endpoint
            .get
            .in("movies")
            .out(jsonBody[List[Movie]])
            .errorOut(stringBody)
    val getMoviesServerEndpoint: ZServerEndpoint[TableRepo[Movie], Any] =
        getMoviesEndpoint.zServerLogic(_ =>
            TableRepo.getAll[Movie]
                .catchAll(
                    e => ZIO.fail(e.getMessage())
                )
        )

    type EndpointsEnv = TableRepo[Movie]

    val all: List[ZServerEndpoint[EndpointsEnv, Any]] =
        List(helloServerEndpoint.widen[EndpointsEnv]) ++
        crudEndpoints[Movie]("movies").map(_.widen[EndpointsEnv])
