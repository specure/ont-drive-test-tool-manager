package com.specure.core.presentation.ui

import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import kotlin.time.Duration

fun Duration.toLocalTime(): LocalTime {
    val instant = Instant.ofEpochMilli(this.inWholeMilliseconds)
    val zoneId = ZoneId.systemDefault()
    val localDateTime = LocalTime.ofInstant(instant, zoneId)
    return localDateTime
}