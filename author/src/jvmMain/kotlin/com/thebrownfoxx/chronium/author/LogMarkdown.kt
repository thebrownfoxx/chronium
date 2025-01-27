package com.thebrownfoxx.chronium.author

import kotlinx.datetime.LocalDate
import java.io.File

data class LogMarkdown(
    val entryDate: LocalDate,
    val content: String,
)

fun getLogMarkdown(): LogMarkdown {
    val logMd = File("../log.md").readLines()
    val entryDate = logMdDateFormat.parse(logMd.first())
    val content = logMd.drop(2).joinToString("\n")
    return LogMarkdown(entryDate, content)
}