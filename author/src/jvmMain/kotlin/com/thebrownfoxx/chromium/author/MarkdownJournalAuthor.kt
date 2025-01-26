package com.thebrownfoxx.chromium.author

import com.thebrownfoxx.chronium.prepare
import com.thebrownfoxx.chronium.summarizer.JournalSummarizer
import com.thebrownfoxx.karbon.markdown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import java.io.File

class MarkdownJournalAuthor(
    private val directory: File,
    private val summarizer: JournalSummarizer,
) : JournalAuthor {
    // We don't inject this since the format MarkdownJournalParser reads is directly bound to what
    // MarkdownJournalAuthor writes
    private val parser = MarkdownJournalParser(directory)

    override suspend fun log(
        text: String,
        entryDate: LocalDate,
        logTime: LocalDateTime,
    ) {
        val entryFile = getEntryFile(entryDate)
        val logTimeString = when (logTime.date) {
            entryDate -> logTimeFormat.format(logTime.time)
            else -> logDateTimeFormat.format(logTime)
        }
        val logMarkdown = markdown {
            whitespace()
            h6(logTimeString)
            whitespace()
            p(text)
        }
        withContext(Dispatchers.IO) {
            entryFile.appendText(logMarkdown.value)
        }
    }

    override suspend fun summarizeEntry(entryDate: LocalDate) {
        val parsedEntries = parser.parseEntries()
        if (parsedEntries.first { it.date == entryDate }.summary != null) error("Entry already has a summary")
        val preparedEntries = parsedEntries.prepare(maxPreviousEntriesWithLogs = 100, dayToSummarize = entryDate)
        val summary = summarizer.summarizeDay(preparedEntries)
        val file = getEntryFile(entryDate)
        val fileExistingLines = withContext(Dispatchers.IO) { file.readLines() }
        val header = fileExistingLines.first()
        val logLines = fileExistingLines.drop(2)
        val summaryMarkdown = markdown {
            whitespace()
            block {
                h3("AI Summary")
                whitespace()
                p(summary)
                whitespace()
                h6 {
                    text("Generated by ")
                    link("https://github.com/thebrownfoxx/chronium", "Chronium")
                }
            }
        }
        val fileFinalLines = header + "\n" + summaryMarkdown.value + logLines.joinToString("\n")
        withContext(Dispatchers.IO) {
            file.writeText(fileFinalLines)
        }
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

    private val fileNameDateFormat = LocalDate.Format {
        year()
        char('-')
        monthNumber()
        char('-')
        dayOfMonth()
    }

    private val entryHeaderDateFormat = LocalDate.Format {
        monthName(MonthNames.ENGLISH_FULL)
        char(' ')
        dayOfMonth(Padding.NONE)
        chars(", ")
        year(Padding.NONE)
    }

    private val logDateTimeFormat = LocalDateTime.Format {
        monthName(MonthNames.ENGLISH_FULL)
        char(' ')
        dayOfMonth(Padding.NONE)
        chars(", ")
        amPmHour(Padding.NONE)
        char(':')
        minute()
        char(' ')
        amPmMarker(am = "am", pm = "pm")
    }

    private val logTimeFormat = LocalTime.Format {
        amPmHour(Padding.NONE)
        char(':')
        minute()
        char(' ')
        amPmMarker(am = "am", pm = "pm")
    }
}