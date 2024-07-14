package com.github.mikolololoay.models


import kantan.csv.RowDecoder


final case class ScreeningRoom(
    id: String,
    roomName: String,
    capacity: Int,
    has3d: Boolean,
    screenType: String,
    audioSystem: String
)


object ScreeningRoom:
    given RowDecoder[ScreeningRoom] = RowDecoder.decoder(0, 1, 2, 3, 4, 5)(ScreeningRoom.apply)