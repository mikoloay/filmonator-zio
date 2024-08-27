package com.github.mikolololoay.http

import com.github.mikolololoay.http.Endpoints.EndpointsEnv
import com.github.mikolololoay.repositories.SessionRepo
import com.github.mikolololoay.views.LoginView
import com.github.mikolololoay.views.PageGenerator
import com.github.mikolololoay.views.UnauthenticatedView
import sttp.model.Header
import sttp.model.Headers
import sttp.model.StatusCode
import sttp.model.headers.CookieValueWithMeta
import sttp.model.headers.CookieWithMeta
import sttp.tapir.generic.auto.*
import sttp.tapir.ztapir.*
import zio.*


/** Provides all endpoints related to authentication.
  * 
  * All UI endpoints are secured by requiring a cookie with
  * a correct session id. Guests can receive such a cookie
  * by filling a form in the /login endpoint and sending it
  * (currently only "admin" + "password" combination works :D).
  * If the credentials are correct, a unique session id will
  * be generated and sent to a cache store using SessionRepo
  * (currently it uses Redis). Session ids from guests' browsers
  * are compared with ids present on Redis.
  * 
  * API Endpoints currently don't require authentication, but
  * someday they will require a valid Bearer token (TODO).
  */
object AuthEndpoints:
    case class LoginForm(username: String, password: String)

    val loginEndpoint: ZServerEndpoint[Any, Any] =
        endpoint.get
            .in("login")
            .out(htmlBodyUtf8)
            .zServerLogic(_ => ZIO.succeed(PageGenerator.generate(LoginView.fullBody).render))

    val validateLoginEndpoint: ZServerEndpoint[SessionRepo, Any] =
        endpoint.post
            .in("login")
            .in(formBody[LoginForm])
            .out(headers)
            .out(stringBody)
            .errorOut(stringBody)
            .zServerLogic {
                case LoginForm("admin", "password") =>
                    (for
                        sessionId <- SessionRepo.createUniqueSession("admin")
                        cookie <- ZIO.fromEither(
                            CookieWithMeta.safeApply(name = "sessionId", value = sessionId.toString)
                        )
                    yield (
                        List(Header.setCookie(cookie), Header("HX-Redirect", "/")),
                        "Login successful!"
                    ))
                        .catchAll:
                            case _ => ZIO.fail("Sorry, we couldn't authenticate you.")
                case _ =>
                    ZIO.succeed(
                        List.empty,
                        "<div style='color: white; background-color: red; padding: 10px; text-align: center;'>Invalid username or password!</div>"
                    )
            }

    val secureEndpoint: ZPartialServerEndpoint[SessionRepo, Option[String], Unit, Unit, String, Unit, Any] =
        endpoint
            .securityIn(auth.apiKey(cookie[Option[String]]("sessionId")))
            .errorOut(htmlBodyUtf8)
            .zServerSecurityLogic(login =>
                (for
                    sessionId <- ZIO.fromOption(login)
                    existsSession <- SessionRepo.verifySession(sessionId)
                    result <- if existsSession then ZIO.unit else ZIO.fail(())
                yield ())
                    .catchAll:
                        case _ => ZIO.fail(PageGenerator.generate(UnauthenticatedView.fullBody).render)
            )

    val all = List(
        loginEndpoint.widen[EndpointsEnv],
        validateLoginEndpoint.widen[EndpointsEnv]
    )
