package de.rki.coronawarnapp.util

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun Instant.toUserTimeZone(): LocalDateTime = LocalDateTime.ofInstant(this, ZoneId.systemDefault())
fun Instant.toLocalDateUtc(): LocalDate = atZone(ZoneOffset.UTC).toLocalDate()
fun LocalDate.ageInDays(now: LocalDate) = Duration.between(this, now).toDays().toInt()
fun Instant.toLocalDateUserTimeZone(): LocalDate = atZone(ZoneId.systemDefault()).toLocalDate()
