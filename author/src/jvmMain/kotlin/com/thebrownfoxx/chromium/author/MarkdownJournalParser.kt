package com.thebrownfoxx.chromium.author

import com.thebrownfoxx.chronium.parser.JournalParser
import com.thebrownfoxx.chronium.parser.ParsedEntry
import com.thebrownfoxx.chronium.parser.ParsedLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import java.io.File

class MarkdownJournalParser(private val directory: File) : JournalParser {
    override suspend fun parseEntries(): List<ParsedEntry> {
        return directory.listFiles().map {
            it.parseFile().parseEntry()
        }
    }

    private suspend fun File.parseFile(): List<String> {
        return withContext(Dispatchers.IO) { readLines() }
    }

    private fun List<String>.parseEntry(): ParsedEntry {
        val date = first().parseEntryDate()
        val summary = filter { it.startsWith(">") }.parseSummary()
        var remainingLogLines = dropWhile { !it.startsWith("######") }
        val logs = mutableListOf<ParsedLog>()
        while (remainingLogLines.isNotEmpty()) {
            val log = listOf(remainingLogLines.first()) +
                    remainingLogLines.drop(1).takeWhile { !it.startsWith("######") }
            logs.add(log.parseLog(date))
            remainingLogLines = remainingLogLines.slice(log.size..remainingLogLines.lastIndex)
        }
        return ParsedEntry(date, summary, logs)
    }

    private fun String.parseEntryDate(): LocalDate {
        val dateString = removePrefix("# ").trim()
        return LocalDate.Companion.parse(dateString, localDateFormat)
    }

    private fun List<String>.parseSummary(): String? {
        val summaryLines = map { it.removePrefix(">").trim() }
            .filter { it.isNotBlank() && !it.startsWith('#') }
        if (summaryLines.isEmpty()) return null
        return summaryLines.joinToString("\n")
    }

    private fun List<String>.parseLog(entryDate: LocalDate): ParsedLog {
        val time = first { it.startsWith("#") }.parseTime(entryDate)
        val text = filter { !it.startsWith("#") && it.isNotBlank() }.joinToString("\n") { it.trim() }
        return ParsedLog(time, text)
    }

    private fun String.parseTime(entryDate: LocalDate): LocalDateTime {
        val timeString = removePrefix("###### ").trim()
        if (timeString.contains(",")) return localDateTimeFormat.parse("${entryDate.year} $timeString")
        return localDateTimeFormat.parseOrNull(timeString)
            ?: (entryDate + localTimeFormat.parse(timeString))
    }

    private val localDateFormat = LocalDate.Companion.Format {
        monthName(MonthNames.Companion.ENGLISH_FULL)
        char(' ')
        dayOfMonth(Padding.NONE)
        chars(", ")
        year(Padding.NONE)
    }

    private val localTimeFormat = LocalTime.Companion.Format {
        amPmHour(Padding.NONE)
        char(':')
        minute()
        char(' ')
        amPmMarker(am = "am", pm = "pm")
    }

    @Suppress("DuplicatedCode")
    private val localDateTimeFormat = LocalDateTime.Companion.Format {
        year(Padding.NONE)
        char(' ')
        monthName(MonthNames.Companion.ENGLISH_FULL)
        char(' ')
        dayOfMonth(Padding.NONE)
        chars(", ")
        amPmHour(Padding.NONE)
        char(':')
        minute()
        char(' ')
        amPmMarker(am = "am", pm = "pm")
    }
}

private operator fun LocalDate.plus(time: LocalTime) = LocalDateTime(this, time)