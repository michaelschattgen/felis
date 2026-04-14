package me.schattgen.felis.ui.home

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
    .withZone(ZoneId.systemDefault())

fun formatDateTime(epochMs: Long): String =
    dateTimeFormatter.format(Instant.ofEpochMilli(epochMs))
