package com.thebrownfoxx.chronium.ui.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thebrownfoxx.chronium.ui.service.JournalManager.NoEntryOnDate
import com.thebrownfoxx.chronium.ui.service.Markdown
import com.thebrownfoxx.outcome.Outcome
import com.thebrownfoxx.outcome.map.fold
import kotlinx.datetime.LocalDate

@Composable
fun JournalScreen(
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    entry: Entry,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        EntryDatePicker(
            date = date,
            onDateChange = onDateChange,
            modifier = Modifier
                .fillMaxHeight()
                .width(360.dp),
        )
        Text(
            text = when (entry) {
                is Entry.Found -> entry.markdown.content
                Entry.NoneOnDate -> "No entry on this date"
                Entry.Loading -> "Loading"
            },
            modifier = Modifier.weight(1f),
        )
    }
}