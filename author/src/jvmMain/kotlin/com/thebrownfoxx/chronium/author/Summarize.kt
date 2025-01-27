package com.thebrownfoxx.chronium.author

suspend fun main() {
    val author = getAuthor()
    val (entryDate) = getLogMarkdown()
    author.summarizeEntry(entryDate)
}