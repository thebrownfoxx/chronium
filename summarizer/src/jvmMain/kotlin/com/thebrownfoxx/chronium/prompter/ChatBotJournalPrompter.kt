package com.thebrownfoxx.chronium.prompter

import com.thebrownfoxx.chronium.chatbot.*
import com.thebrownfoxx.chronium.summarizer.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

class ChatBotJournalPrompter(
    private val chatBot: ChatBot,
) : JournalPrompter {
    val messages = mutableListOf<Message>()

    override suspend fun start(entries: List<PreparedEntry<PreparedEntryContent>>): String {
        val conversation = conversation {
            User.said("Here is my journal. I want to recall/query things about it. Are you ready to help me?")
            entries.forEach { say(it) }
            Model.said("I am ready to answer your questions about your journal.")
        }
        messages.addAll(conversation.messages)
        return conversation.messages.last().content
    }

    override suspend fun prompt(text: String): String {
        messages.add(Message(User, text))
        return chatBot.prompt(Conversation(messages)).also { response ->
            messages.add(Message(Model, response))
        }
    }

    private fun ConversationBuilder.say(entry: PreparedEntry<PreparedEntryContent>) {
        User.said(entry.date.format(localDateFormat))
        when (val content = entry.content) {
            is PreparedSummary -> say(content)
            is PreparedLogs -> say(content.values)
        }
    }

    private fun ConversationBuilder.say(summary: PreparedSummary) {
        User.said("Summary: ")
        User.said(summary.value)
    }

    private fun ConversationBuilder.say(logs: List<PreparedLog>) {
        logs.forEach { log ->
            User.said("Logged  ${log.time.format(localDateTimeFormat)}: ")
            User.said(log.text)
        }
    }

    private val localDateFormat = LocalDate.Format {
        monthName(MonthNames.ENGLISH_FULL)
        char(' ')
        dayOfMonth(Padding.NONE)
        chars(", ")
        year(Padding.NONE)
    }

    private val localDateTimeFormat = LocalDateTime.Format {
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
}