package com.thebrownfoxx.chronium.ui.service

import com.thebrownfoxx.outcome.Outcome
import com.thebrownfoxx.outcome.UnitOutcome
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface JournalManager {
    suspend fun getEntry(date: LocalDate): Outcome<Markdown, NoEntryOnDate>

    suspend fun log(
        text: String,
        entryDate: LocalDate,
        logTime: LocalDateTime,
    ): UnitOutcome<LogError>

    data object NoEntryOnDate

    data object LogError
}