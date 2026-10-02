package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MealType
import com.example.ui.MealViewModel
import com.example.ui.components.AllergySettingsDialog
import com.example.ui.components.DatePickerModal
import com.example.ui.components.EmptyMealState
import com.example.ui.components.MealCard
import com.example.ui.components.NutritionDialog
import com.example.ui.components.OriginDialog
import com.example.ui.components.SchoolInfoDialog
import com.example.ui.components.WeeklyCalendarStrip
import com.example.ui.theme.AllergyAlertRed
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

  private val viewModel: MealViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MealAppScreen(viewModel = viewModel)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealAppScreen(viewModel: MealViewModel) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  var isSchoolInfoOpen by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "대진전자통신고 급식",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "나이스(NEIS) 실시간 급식알리미",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        actions = {
          // Allergy Setting Button with active badge
          IconButton(
            onClick = { viewModel.setAllergyDialogOpen(true) },
            modifier = Modifier.testTag("open_allergy_settings_button")
          ) {
            BadgedBox(
              badge = {
                if (uiState.userAllergyIds.isNotEmpty()) {
                  Badge(
                    containerColor = AllergyAlertRed,
                    contentColor = Color.White
                  ) {
                    Text(uiState.userAllergyIds.size.toString())
                  }
                }
              }
            ) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "알레르기 설정",
                tint = if (uiState.userAllergyIds.isNotEmpty()) AllergyAlertRed
                else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Refresh Button
          IconButton(
            onClick = { viewModel.refresh() },
            modifier = Modifier.testTag("refresh_button")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "새로고침",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // School Info Button
          IconButton(
            onClick = { isSchoolInfoOpen = true },
            modifier = Modifier.testTag("open_school_info_button")
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = "학교 정보",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    floatingActionButton = {
      if (!uiState.isToday) {
        ExtendedFloatingActionButton(
          onClick = { viewModel.goToToday() },
          icon = { Icon(Icons.Default.Today, contentDescription = null) },
          text = { Text("오늘 급식으로") },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary,
          modifier = Modifier.testTag("fab_go_to_today")
        )
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      // Loading Bar
      if (uiState.isLoading) {
        LinearProgressIndicator(
          modifier = Modifier.fillMaxWidth(),
          color = MaterialTheme.colorScheme.primary
        )
      }

      // Weekly Calendar Strip
      WeeklyCalendarStrip(
        selectedDate = uiState.selectedDate,
        onDateSelected = { viewModel.selectDate(it) },
        onPrevDay = { viewModel.goToPreviousDay() },
        onNextDay = { viewModel.goToNextDay() },
        onToday = { viewModel.goToToday() },
        onOpenDatePicker = { viewModel.setDatePickerOpen(true) }
      )

      // Meal Type Filter Chips (전체, 조식, 중식, 석식)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        FilterChip(
          selected = uiState.selectedMealFilter == null,
          onClick = { viewModel.setMealFilter(null) },
          label = { Text("전체") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
          ),
          modifier = Modifier.testTag("meal_filter_all")
        )

        MealType.entries.forEach { type ->
          val isSelected = uiState.selectedMealFilter == type
          FilterChip(
            selected = isSelected,
            onClick = {
              viewModel.setMealFilter(if (isSelected) null else type)
            },
            leadingIcon = {
              Text(text = type.emoji, fontSize = 12.sp)
            },
            label = { Text(type.displayName) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.testTag("meal_filter_${type.code}")
          )
        }
      }

      // Offline warning / error banner if any
      if (uiState.errorMessage != null && !uiState.isLoading) {
        Surface(
          color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
          shape = RoundedCornerShape(8.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = uiState.errorMessage ?: "",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.secondary
            )
          }
        }
      }

      // Meal List or Empty State
      if (uiState.meals.isEmpty() && !uiState.isLoading) {
        EmptyMealState(
          selectedDate = uiState.selectedDate,
          onGoToToday = { viewModel.goToToday() },
          onGoToNextDay = { viewModel.goToNextDay() },
          onOpenDatePicker = { viewModel.setDatePickerOpen(true) },
          onRefresh = { viewModel.refresh() }
        )
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(bottom = 80.dp, top = 4.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(uiState.meals, key = { "${it.date}_${it.mealCode}" }) { meal ->
            MealCard(
              meal = meal,
              userAllergyIds = uiState.userAllergyIds,
              onViewNutrition = { viewModel.showNutrition(meal) },
              onViewOrigin = { viewModel.showOrigin(meal) }
            )
          }
        }
      }
    }
  }

  // Dialogs
  if (uiState.isDatePickerOpen) {
    DatePickerModal(
      initialDate = uiState.selectedDate,
      onDateSelected = { viewModel.selectDate(it) },
      onDismiss = { viewModel.setDatePickerOpen(false) }
    )
  }

  if (uiState.isAllergyDialogOpen) {
    AllergySettingsDialog(
      selectedAllergyIds = uiState.userAllergyIds,
      onToggleAllergy = { viewModel.toggleAllergy(it) },
      onClearAll = { viewModel.clearAllergies() },
      onDismiss = { viewModel.setAllergyDialogOpen(false) }
    )
  }

  if (isSchoolInfoOpen) {
    SchoolInfoDialog(onDismiss = { isSchoolInfoOpen = false })
  }

  uiState.activeNutritionMeal?.let { meal ->
    NutritionDialog(
      meal = meal,
      onDismiss = { viewModel.showNutrition(null) }
    )
  }

  uiState.activeOriginMeal?.let { meal ->
    OriginDialog(
      meal = meal,
      onDismiss = { viewModel.showOrigin(null) }
    )
  }
}
