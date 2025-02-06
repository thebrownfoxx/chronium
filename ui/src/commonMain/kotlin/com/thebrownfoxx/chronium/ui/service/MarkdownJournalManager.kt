package com.thebrownfoxx.chronium.ui.service

import com.thebrownfoxx.chronium.ui.service.JournalManager.LogError
import com.thebrownfoxx.chronium.ui.service.JournalManager.NoEntryOnDate
import com.thebrownfoxx.karbon.markdown
import com.thebrownfoxx.outcome.Outcome
import com.thebrownfoxx.outcome.UnitOutcome
import com.thebrownfoxx.outcome.map.mapError
import com.thebrownfoxx.outcome.runFailing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import java.io.File

class MarkdownJournalManager(
    private val directory: File,
) : JournalManager {
    override suspend fun getEntry(date: LocalDate): Outcome<Markdown, NoEntryOnDate> {
        return runFailing {
            withContext(Dispatchers.IO) {
                val formattedDate = date.format(fileNameDateFormat)
                val file = directory.resolve("${formattedDate}.md")
                Markdown(file.readLines().drop(2).joinToString("\n"))
            }
        }.mapError { NoEntryOnDate }
    }

    override suspend fun log(
        text: String,
        entryDate: LocalDate,
        logTime: LocalDateTime,
    ): UnitOutcome<LogError> {
        return runFailing {
            val entryFile = getEntryFile(entryDate)
            val logTimeString = when (logTime.date) {
                entryDate -> logTimeFormat.format(logTime.time)
                else -> logDateTimeFormat.format(logTime)
            }
            val logMarkdown = markdown {
                whitespace()
                h6(logTimeString)
                whitespace()
                markdown { text }
            }
            withContext(Dispatchers.IO) { entryFile.appendText(logMarkdown.value) }
        }.mapError { LogError }
    }

    private suspend fun getEntryFile(entryDate: LocalDate): File {
        return withContext(Dispatchers.IO) {
            directory.mkdirs()
            val dateString = fileNameDateFormat.format(entryDate)
            val file = directory.resolve("$dateString.md")
            if (!file.exists()) {
                val headerDateString = entryHeaderDateFormat.format(entryDate)
                val headerMarkdown = markdown { h1(headerDateString) }
                file.writeText(headerMarkdown.value)
            }
            file
        }
    }

    private val fileNameDateFormat = LocalDate.Companion.Format {
        year()
        char('-')
        monthNumber()
        char('-')
        dayOfMonth()
    }

    private val entryHeaderDateFormat = LocalDate.Companion.Format {
        monthName(MonthNames.Companion.ENGLISH_FULL)
        char(' ')
        dayOfMonth(Padding.NONE)
        chars(", ")
        year(Padding.NONE)
    }

    private val logDateTimeFormat = LocalDateTime.Companion.Format {
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

    private val logTimeFormat = LocalTime.Companion.Format {
        amPmHour(Padding.NONE)
        char(':')
        minute()
        char(' ')
        amPmMarker(am = "am", pm = "pm")
    }
}