package com.thebrownfoxx.chronium

import com.thebrownfoxx.chronium.parser.ParsedEntry
import com.thebrownfoxx.chronium.parser.ParsedLog
import com.thebrownfoxx.chronium.summarizer.*
import kotlinx.datetime.LocalDate

fun List<ParsedEntry>.prepare(
    maxPreviousEntriesWithLogs: Int = Int.MAX_VALUE,
    dayToSummarize: LocalDate? = null,
): PreparedEntries {
    val entriesToPrepare = sortedBy { it.date }.dropLastWhile { dayToSummarize != null && it.date != dayToSummarize }
    var remainingPreviousEntriesWithLogs = maxPreviousEntriesWithLogs
    val previousEntries = entriesToPrepare.dropLast(1)
    val preparedPreviousEntries = previousEntries.mapIndexed { index, entry ->
        val distanceFromLast = previousEntries.lastIndex - index
        if (entry.summary == null || distanceFromLast < remainingPreviousEntriesWithLogs)
            entry.prepareWithLogs().also { remainingPreviousEntriesWithLogs-- }
        else PreparedEntry(entry.date, PreparedSummary(entry.summary))
    }
    return PreparedEntries(
        previousEntries = preparedPreviousEntries,
        entryToSummarize = entriesToPrepare.last().prepareWithLogs(),
    )
}

fun ParsedEntry.prepareWithLogs(): PreparedEntry<PreparedLogs> {
    return PreparedEntry(date, PreparedLogs(logs.map { it.prepare() }))
}

fun ParsedLog.prepare(): PreparedLog {
    return PreparedLog(
        time = time,
        text = text,
    )
}