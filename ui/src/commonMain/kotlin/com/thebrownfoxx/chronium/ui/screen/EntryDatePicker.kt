package com.thebrownfoxx.chronium.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDatePicker(
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberDatePickerState(initialSelectedDateMillis = date.toEpochMillis())

    val selectedDateMillis = state.selectedDateMillis
    LaunchedEffect(selectedDateMillis) {
        if (selectedDateMillis == null) return@LaunchedEffect
        onDateChange(LocalDate.fromEpochMillis(selectedDateMillis))
    }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Box {
            DatePicker(
                state = state,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}

private fun LocalDate.toEpochMillis() = LocalDateTime(
    date = this,
    time = LocalTime.fromSecondOfDay(0),
).toInstant(TimeZone.UTC).toEpochMilliseconds()

private fun LocalDate.Companion.fromEpochMillis(epochMillis: Long) =
    Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.UTC).date
