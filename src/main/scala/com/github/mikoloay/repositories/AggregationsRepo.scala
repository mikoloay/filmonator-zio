package com.github.mikoloay.repositories

import com.github.mikoloay.utils.DateUtils
import java.sql.SQLException
import zio.*
import io.getquill.jdbczio.Quill
import io.getquill.*
import com.github.mikoloay.models.{Movie, Screening, ScreeningRoom, Ticket, TicketTransaction}
import com.github.mikoloay.models.aggregations.ScreeningWithDetails


trait AggregationsRepo:
    def getScreeningsWithDetailsForDate(date: DateUtils.Date): ZIO[Any, SQLException, List[ScreeningWithDetails]]


object AggregationsRepo:
    def getScreeningsWithDetailsForDate(date: DateUtils.Date): ZIO[AggregationsRepo, SQLException, List[ScreeningWithDetails]] =
        ZIO.serviceWithZIO[AggregationsRepo](_.getScreeningsWithDetailsForDate(date))

    val layer: ZLayer[Quill.Postgres[SnakeCase], Nothing, AggregationsRepo] =
        ZLayer.fromFunction(quill => new AggregationsRepoImpl(quill))


class AggregationsRepoImpl(quill: Quill.Postgres[SnakeCase]) extends AggregationsRepo:
    import quill.*

    override def getScreeningsWithDetailsForDate(date: DateUtils.Date) = 
        run:
            (for
                s <- query[Screening] if s.date == lift(date.value)
                sr <- query[ScreeningRoom].join(sr => sr.id == s.roomId)
                m <- query[Movie].join(m => m.id == s.movieId)
                tt <- query[TicketTransaction].join(tt => tt.screeningId == s.screeningId)
                t <- query[Ticket].join(t => t.name == tt.ticketType)
            yield (s, sr, m, tt, t))
            .groupByMap((s, sr, m, tt, t) => (s.screeningId, s.date, sr.roomName, m.name))((s, sr, m, tt, t) =>
                ScreeningWithDetails(s.date, sr.roomName, m.name, count(tt.id).toInt, sum(t.priceInZloty))
            )
