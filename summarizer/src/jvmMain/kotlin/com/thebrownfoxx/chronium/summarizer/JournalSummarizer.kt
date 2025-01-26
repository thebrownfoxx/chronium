package com.thebrownfoxx.chronium.summarizer

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface JournalSummarizer {
    suspend fun summarizeDay(preparedEntries: PreparedEntries): String
}

data class PreparedEntries(
    val previousEntries: List<PreparedEntry<PreparedEntryContent>>,
    val entryToSummarize: PreparedEntry<PreparedLogs>,
)

data class PreparedEntry<out T : PreparedEntryContent>(
    val date: LocalDate,
    val content: T,
)

sealed interface PreparedEntryContent

data class PreparedSummary(val value: String) : PreparedEntryContent

data class PreparedLogs(val values: List<PreparedLog>) : PreparedEntryContent

data class PreparedLog(
    val time: LocalDateTime,
    val text: String,
)