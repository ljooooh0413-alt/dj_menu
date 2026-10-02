package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WeeklyCalendarStrip(
  selectedDate: LocalDate,
  onDateSelected: (LocalDate) -> Unit,
  onPrevDay: () -> Unit,
  onNextDay: () -> Unit,
  onToday: () -> Unit,
  onOpenDatePicker: () -> Unit,
  modifier: Modifier = Modifier
) {
  val today = LocalDate.now()
  val startOfWeek = selectedDate.minusDays((selectedDate.dayOfWeek.value - 1).toLong())
  val daysOfWeek = (0..6).map { startOfWeek.plusDays(it.toLong()) }

  val monthTitleFormatter = DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREAN)
  val dateTitleFormatter = DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN)

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 2.dp,
    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      // Month and Navigation Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onOpenDatePicker)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("month_title_picker")
        ) {
          Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = "달력 열기",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.size(6.dp))
          Text(
            text = selectedDate.format(monthTitleFormatter),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (selectedDate != today) {
            TextButton(
              onClick = onToday,
              modifier = Modifier.testTag("go_to_today_button")
            ) {
              Icon(
                imageVector = Icons.Default.Today,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.size(4.dp))
              Text("오늘", style = MaterialTheme.typography.labelMedium)
            }
          }

          IconButton(
            onClick = onPrevDay,
            modifier = Modifier
              .size(36.dp)
              .testTag("prev_day_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "이전 날",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(
            onClick = onNextDay,
            modifier = Modifier
              .size(36.dp)
              .testTag("next_day_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = "다음 날",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 7-day strip (Mon to Sun)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        daysOfWeek.forEach { date ->
          val isSelected = date == selectedDate
          val isTodayDate = date == today
          val isWeekend = date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY

          val dayOfWeekName = when (date.dayOfWeek) {
            DayOfWeek.MONDAY -> "월"
            DayOfWeek.TUESDAY -> "화"
            DayOfWeek.WEDNESDAY -> "수"
            DayOfWeek.THURSDAY -> "목"
            DayOfWeek.FRIDAY -> "금"
            DayOfWeek.SATURDAY -> "토"
            DayOfWeek.SUNDAY -> "일"
          }

          val labelColor = when {
            isSelected -> MaterialTheme.colorScheme.onPrimary
            date.dayOfWeek == DayOfWeek.SUNDAY -> Color(0xFFE53935)
            date.dayOfWeek == DayOfWeek.SATURDAY -> Color(0xFF1E88E5)
            else -> MaterialTheme.colorScheme.onSurfaceVariant
          }

          val numberColor = when {
            isSelected -> MaterialTheme.colorScheme.onPrimary
            else -> MaterialTheme.colorScheme.onSurface
          }

          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else if (isTodayDate) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                else Color.Transparent
              )
              .clickable { onDateSelected(date) }
              .padding(horizontal = 10.dp, vertical = 8.dp)
              .testTag("day_item_${date.dayOfMonth}")
          ) {
            Text(
              text = dayOfWeekName,
              fontSize = 12.sp,
              fontWeight = if (isSelected || isTodayDate) FontWeight.Bold else FontWeight.Medium,
              color = labelColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier.size(26.dp)
            ) {
              Text(
                text = date.dayOfMonth.toString(),
                fontSize = 15.sp,
                fontWeight = if (isSelected || isTodayDate) FontWeight.Bold else FontWeight.Normal,
                color = numberColor
              )
            }

            // Dot for today
            if (isTodayDate) {
              Box(
                modifier = Modifier
                  .padding(top = 2.dp)
                  .size(4.dp)
                  .clip(CircleShape)
                  .background(
                    if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.primary
                  )
              )
            } else {
              Spacer(modifier = Modifier.height(6.dp))
            }
          }
        }
      }
    }
  }
}
