package com.thebrownfoxx.chronium.author

import com.thebrownfoxx.chronium.chatbot.GeminiChatBot
import com.thebrownfoxx.chronium.parser.MarkdownJournalParser
import com.thebrownfoxx.chronium.prepareWithLogs
import com.thebrownfoxx.chronium.prompter.ChatBotJournalPrompter
import kotlinx.coroutines.runBlocking
import java.io.File

fun main() = runBlocking {
    val localProperties = getLocalProperties()
    val apiKey = localProperties["geminiKey"].toString()
    val chatBot = GeminiChatBot(DefaultHttpClient, apiKey)
    val directory = File(localProperties["directory"].toString())
    val parser = MarkdownJournalParser(directory)
    val prompter = ChatBotJournalPrompter(chatBot)
    val entries = parser.parseEntries().map { it.prepareWithLogs() }
    val response = prompter.start(entries)
    print("AI: $response\n")

    var prompt: String
    do {
        print("You: ")
        prompt = readln()
        val response = prompter.prompt(prompt)
        println("\nAI: $response")
    } while (response != ":q!")
    println("AI: Bye!")
}