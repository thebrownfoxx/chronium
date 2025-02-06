package com.thebrownfoxx.chronium.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebrownfoxx.chronium.ui.service.JournalManager
import com.thebrownfoxx.chronium.ui.service.Markdown
import com.thebrownfoxx.outcome.map.fold
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class JournalViewModel(private val journalManager: JournalManager) : ViewModel() {
    private val _date = MutableStateFlow(getCurrentDate())
    val date = _date.asStateFlow()

    val entry = _date.map { selectedDate ->
        journalManager.getEntry(selectedDate).fold(
            onSuccess = { Entry.Found(it) },
            onFailure = { Entry.NoneOnDate },
        )
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Entry.Loading)

    fun onDateChange(date: LocalDate) {
        _date.value = date
    }

    private fun getCurrentDate() = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
}

sealed interface Entry {
    data object Loading : Entry
    data object NoneOnDate : Entry
    data class Found(val markdown: Markdown) : Entry
}