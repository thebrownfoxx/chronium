package com.thebrownfoxx.chronium.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thebrownfoxx.chronium.ui.localProperties
import com.thebrownfoxx.chronium.ui.service.MarkdownJournalManager
import java.io.File

@Composable
fun StatefulJournalScreen() {
    val viewModel = viewModel {
        val directory = File(localProperties["directory"].toString())
        JournalViewModel(MarkdownJournalManager(directory))
    }

    with(viewModel) {
        val selectedDate by date.collectAsStateWithLifecycle()
        val entry by entry.collectAsStateWithLifecycle()

        JournalScreen(
            date = selectedDate,
            onDateChange = ::onDateChange,
            entry = entry,
        )
    }
}