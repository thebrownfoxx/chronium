package com.thebrownfoxx.chromium.author

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.char
import java.io.File

suspend fun main() {
    val author = getAuthor()
    val logMd = File("../log.md").readLines()
    val entryDate = logMdDateFormat.parse(logMd.first())
    val log = logMd.drop(2).joinToString("\n")
    author.log(
        text = log,
        entryDate = entryDate,
    )
}

private val logMdDateFormat = LocalDate.Format {
    year()
    char('-')
    monthNumber()
    char('-')
    dayOfMonth()
}