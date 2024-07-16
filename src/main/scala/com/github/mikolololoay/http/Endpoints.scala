package com.github.mikolololoay.http


import sttp.tapir.PublicEndpoint
import sttp.tapir.ztapir.*
import sttp.tapir.generic.auto.*
import zio.*
import com.github.mikolololoay.repositories.TableRepo
import com.github.mikolololoay.models.Movie
import sttp.tapir.json.zio.*


object Endpoints:
    val helloEndpoint: PublicEndpoint[String, Unit, String, Any] =
        endpoint
            .get
            .in("hello" / query[String]("name"))
            .out(stringBody)
    val helloServerEndpoint: ZServerEndpoint[Any, Any] =
        helloEndpoint.serverLogicSuccess(name => ZIO.succeed(s"Siemanko $name"))

    val getMoviesEndpoint: PublicEndpoint[Unit, Unit, List[Movie], Any] =
        endpoint
            .get
            .in("movies")
            .out(jsonBody[List[Movie]])
    val getMoviesServerEndpoint: ZServerEndpoint[TableRepo[Movie], Any] =
        getMoviesEndpoint.serverLogicSuccess(_ =>
            TableRepo.getAll[Movie]
        )
    
    val all: List[ZServerEndpoint[TableRepo[Movie], Any]] = List(
        // helloServerEndpoint,
        getMoviesServerEndpoint
    )