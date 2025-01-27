package com.thebrownfoxx.chronium.author

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.char

suspend fun main() {
    val author = getAuthor()
    val (entryDate, log) = getLogMarkdown()
    author.log(
        text = log,
        entryDate = entryDate,
    )
}

val logMdDateFormat = LocalDate.Format {
    year()
    char('-')
    monthNumber()
    char('-')
    dayOfMonth()
}