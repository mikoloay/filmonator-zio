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
import zio.http.template.*
import zio.http.Handler
import zio.http.template.Element.PartialElement


object Endpoints:
    def crudEndpoints[A: JsonEncoder: JsonDecoder: Schema: Tag](
            endpointName: String
    ) =
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

        val endpoints = List(
            getAllEndpoint,
            getByIdEndpoint
        )
        val serverEndpoints = List(
            getAllServerEndpoint,
            getByIdServerEndpoint
        )
        (endpoints, serverEndpoints)

    val rootEndpoint: PublicEndpoint[Unit, Unit, String, Any] =
        endpoint.get
            .in("")
            .out(htmlBodyUtf8)

    val rootServerEndpoint: ZServerEndpoint[Any, Any] =
        rootEndpoint.zServerLogic(_ =>
            ZIO.succeed:
                html(
                    head(
                        title("FILMONATOR APP")
                    ),
                    body(
                        h1("WITAAAM"),
                        PartialElement("h3")("Siema.")
                    )
                ).encode.toString
        )

    type EndpointsEnv = TableRepo[Movie]

    val all: List[ZServerEndpoint[EndpointsEnv, Any]] =
        val (endpoints, serverEndpoints) = crudEndpoints[Movie]("movies")

        rootServerEndpoint.widen[EndpointsEnv] :: serverEndpoints.map(_.widen[EndpointsEnv])
