package com.thebrownfoxx.chromium.author

import com.thebrownfoxx.chronium.chatbot.GeminiChatBot
import com.thebrownfoxx.chronium.summarizer.ChatBotJournalSummarizer
import java.io.File

suspend fun getAuthor(): MarkdownJournalAuthor {
    val localProperties = getLocalProperties()
    val apiKey = localProperties["geminiKey"].toString()
    val chatBot = GeminiChatBot(DefaultHttpClient, apiKey)
    val summarizer = ChatBotJournalSummarizer(chatBot)
    val directory = localProperties["directory"].toString()
    val author = MarkdownJournalAuthor(File(directory), summarizer)
    return author
}