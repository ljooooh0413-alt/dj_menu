package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun EmptyMealState(
  selectedDate: LocalDate,
  onGoToToday: () -> Unit,
  onGoToNextDay: () -> Unit,
  onOpenDatePicker: () -> Unit,
  onRefresh: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isWeekend = selectedDate.dayOfWeek == DayOfWeek.SATURDAY || selectedDate.dayOfWeek == DayOfWeek.SUNDAY
  val title = if (isWeekend) "즐거운 주말입니다!" else "급식 정보가 등록되지 않았습니다"
  val subtitle = if (isWeekend) {
    "주말에는 학교 급식이 제공되지 않습니다.\n월요일 급식을 미리 확인해보세요!"
  } else {
    "해당 날짜는 급식이 없거나, 학사 일정(재량휴업일, 방학, 시험일 등)으로 인해 나이스에 식단이 등록되지 않았습니다."
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp)
      .testTag("empty_meal_card"),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Generated illustration
      Image(
        painter = painterResource(id = R.drawable.no_meal_illustration_1790925304086),
        contentDescription = "급식 없음 일러스트",
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(160.dp)
          .clip(RoundedCornerShape(20.dp))
      )

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Helpful quick action buttons
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedButton(
          onClick = onRefresh,
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("empty_refresh_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("새로고침")
        }

        Button(
          onClick = onGoToNextDay,
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("empty_next_day_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Text("다음날 보기")
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      OutlinedButton(
        onClick = onOpenDatePicker,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("empty_date_picker_button"),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(
          imageVector = Icons.Default.CalendarMonth,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text("다른 날짜 선택하기")
      }
    }
  }
}
