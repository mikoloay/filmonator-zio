package com.github.mikolololoay.utils.redis

import zio.redis.CodecSupplier
import zio.schema.Schema
import zio.schema.codec.BinaryCodec
import zio.schema.codec.ProtobufCodec


/** Provides a codec required by ZIO Redis. */
object ProtobufCodecSupplier extends CodecSupplier:
    def get[A: Schema]: BinaryCodec[A] = ProtobufCodec.protobufCodec
