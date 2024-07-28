package com.github.mikolololoay.models

import kantan.csv.RowDecoder
import zio.json.{JsonEncoder, JsonDecoder}


final case class ScreeningRoom(
    id: String,
    roomName: String,
    capacity: Int,
    has3d: Boolean,
    screenType: String,
    audioSystem: String
) derives JsonEncoder,
        JsonDecoder


object ScreeningRoom:
    given RowDecoder[ScreeningRoom] = RowDecoder.decoder(0, 1, 2, 3, 4, 5)(ScreeningRoom.apply)
