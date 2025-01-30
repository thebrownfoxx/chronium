package com.thebrownfoxx.chronium.prompter

import com.thebrownfoxx.chronium.summarizer.PreparedEntry
import com.thebrownfoxx.chronium.summarizer.PreparedEntryContent

interface JournalPrompter {
    suspend fun start(entries: List<PreparedEntry<PreparedEntryContent>>): String
    suspend fun prompt(text: String): String
}