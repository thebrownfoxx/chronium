package com.thebrownfoxx.chronium.author

import kotlinx.datetime.*

interface JournalAuthor {
    suspend fun log(
        text: String,
        entryDate: LocalDate = localDateTimeNow().date,
        logTime: LocalDateTime = localDateTimeNow(),
    )

    suspend fun summarizeEntry(entryDate: LocalDate = localDateTimeNow().date)
}

private fun localDateTimeNow() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())