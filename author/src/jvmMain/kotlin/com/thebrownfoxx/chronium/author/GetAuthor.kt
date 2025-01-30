package com.thebrownfoxx.chronium.author

import com.thebrownfoxx.chronium.chatbot.GeminiChatBot
import com.thebrownfoxx.chronium.summarizer.ChatBotJournalSummarizer
import java.io.File

suspend fun getAuthor(): MarkdownJournalAuthor {
    val localProperties = getLocalProperties()
    val apiKey = localProperties["geminiKey"].toString()
    val chatBot = GeminiChatBot(DefaultHttpClient, apiKey)
    val summarizer = ChatBotJournalSummarizer(chatBot)
    val directory = File(localProperties["directory"].toString())
    val author = MarkdownJournalAuthor(directory, summarizer)
    return author
}