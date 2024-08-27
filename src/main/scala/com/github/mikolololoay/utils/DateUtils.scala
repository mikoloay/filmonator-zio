package com.github.mikolololoay.utils

import scala.util.matching.Regex

/** Provides utilities in form of opaque types to have type safe date related strings. */
object DateUtils:
    opaque type Date = String
    object Date:
        def apply(s: String): Option[Date] =
            val dateFormat: Regex = """\d{4}-\d{2}-\d{2}""".r // yyyy-mm-dd
            s match
                case dateFormat() => Some(s)
                case _            => None
    extension (d: Date) def value: String = d
