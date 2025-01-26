package com.thebrownfoxx.chromium.author

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface JournalAuthor {
    suspend fun log(
        text: String,
        entryDate: LocalDate = localDateTimeNow().date,
        logTime: LocalDateTime = localDateTimeNow(),
    )

    suspend fun summarizeEntry(entryDate: LocalDate = localDateTimeNow().date)
}

private fun localDateTimeNow() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())