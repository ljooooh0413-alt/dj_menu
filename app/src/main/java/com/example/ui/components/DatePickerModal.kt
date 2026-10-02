package com.example.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
  initialDate: LocalDate,
  onDateSelected: (LocalDate) -> Unit,
  onDismiss: () -> Unit
) {
  val initialEpochMillis = initialDate
    .atStartOfDay(ZoneId.of("UTC"))
    .toInstant()
    .toEpochMilli()

  val datePickerState = rememberDatePickerState(
    initialSelectedDateMillis = initialEpochMillis
  )

  DatePickerDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(
        onClick = {
          datePickerState.selectedDateMillis?.let { millis ->
            val selected = Instant.ofEpochMilli(millis)
              .atZone(ZoneId.of("UTC"))
              .toLocalDate()
            onDateSelected(selected)
          }
          onDismiss()
        },
        modifier = Modifier.testTag("date_picker_confirm_button")
      ) {
        Text("선택")
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("date_picker_cancel_button")
      ) {
        Text("취소")
      }
    },
    modifier = Modifier.testTag("date_picker_dialog")
  ) {
    DatePicker(state = datePickerState)
  }
}
