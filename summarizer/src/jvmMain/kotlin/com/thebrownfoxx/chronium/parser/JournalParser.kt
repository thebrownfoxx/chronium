package com.thebrownfoxx.chronium.parser

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface JournalParser {
    suspend fun parseEntries(): List<ParsedEntry>
}

data class ParsedEntry(
    val date: LocalDate,
    val summary: String?,
    val logs: List<ParsedLog>,
)

data class ParsedLog(
    val time: LocalDateTime,
    val text: String,
)