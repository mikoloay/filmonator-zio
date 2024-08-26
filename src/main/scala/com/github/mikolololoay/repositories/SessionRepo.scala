package com.github.mikolololoay.repositories


import zio.redis.*
import zio.*
import java.util.UUID


trait SessionRepo:
    def createUniqueSession(username: String): ZIO[Any, Any, UUID]
    def verifySession(sessionId: String): ZIO[Any, Any, Boolean]


object SessionRepo:
    def createUniqueSession(username: String): ZIO[SessionRepo, Any, UUID] =
        ZIO.serviceWithZIO[SessionRepo](_.createUniqueSession(username))

    def verifySession(sessionId: String): ZIO[SessionRepo, Any, Boolean] =
        ZIO.serviceWithZIO[SessionRepo](_.verifySession(sessionId))

    val layer: ZLayer[Redis, RedisError, SessionRepo] =
        ZLayer.fromFunction(redis => new SessionRepoImpl(redis))


class SessionRepoImpl(redis: Redis) extends SessionRepo:
    val sessionsHash = "sessions"

    override def createUniqueSession(username: String) =
        for
            uuid <- Random.nextUUID
            _ <- redis.hSet(sessionsHash, (uuid, username))
        yield uuid

    override def verifySession(sessionId: String) =
        for
            session <- redis.hGet(sessionsHash, sessionId).returning[String]
        yield session.isDefined