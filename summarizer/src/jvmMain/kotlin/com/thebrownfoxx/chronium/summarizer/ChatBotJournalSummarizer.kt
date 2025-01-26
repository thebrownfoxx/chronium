package com.thebrownfoxx.chronium.summarizer

import com.thebrownfoxx.chronium.chatbot.ChatBot
import com.thebrownfoxx.chronium.chatbot.ConversationBuilder
import com.thebrownfoxx.chronium.chatbot.User
import com.thebrownfoxx.chronium.chatbot.conversation
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

class ChatBotJournalSummarizer(val chatBot: ChatBot) : JournalSummarizer {
    override suspend fun summarizeDay(preparedEntries: PreparedEntries): String {
        val (previousEntries, entryToSummarize) = preparedEntries

        val conversation = conversation {
            User.said("I am writing a journal.")
            User.said("I want you to summarize my last entry.")

            if (previousEntries.isNotEmpty()) {
                if (previousEntries.all { it.content is PreparedSummary }) {
                    User.said("Here are the summaries of my previous entries.")
                } else if (previousEntries.any { it.content is PreparedSummary }) {
                    User.said("Here are my previous entries. Some of them are already summarized.")
                }
                previousEntries.forEach { say(it) }
            }

            User.said("Here is my last entry. I want you to summarize this one.")
            say(entryToSummarize)
            User.said("Model your summary into something like this:")
            User.said("The author experienced a disrupted but ultimately sufficient sleep, waking up slightly behind schedule. They attempted a meditation exercise with limited success and later made good progress on a project called Neon, realizing the need for more consistent therapy exercises. After feeling frustrated, the author shifted focus to other projects and started a script to summarize journal entries, which is working well despite being manual, and ended the day resolving to improve the script and go to bed.")
            User.said("Call me \"the author\".")
            User.said("Output one block for the whole thing, regardless of how many logs there are for this entry.")
            User.said("DO NOT use any formatting.")
        }
        return chatBot.prompt(conversation)
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