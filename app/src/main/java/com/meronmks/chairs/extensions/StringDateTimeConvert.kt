package com.meronmks.chairs.extensions

import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * Created by meron on 2018/01/12.
 */

fun String.toIsoZonedDateTime() : ZonedDateTime{
    return ZonedDateTime.parse(this, DateTimeFormatter.ISO_ZONED_DATE_TIME)
}