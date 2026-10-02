package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.MealEntity
import com.example.data.model.MealType
import com.example.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class MealScreenUiState(
  val selectedDate: LocalDate = LocalDate.now(),
  val isToday: Boolean = true,
  val selectedMealFilter: MealType? = null,
  val meals: List<MealEntity> = emptyList(),
  val isLoading: Boolean = false,
  val errorMessage: String? = null,
  val userAllergyIds: Set<Int> = emptySet(),
  val isAllergyDialogOpen: Boolean = false,
  val isDatePickerOpen: Boolean = false,
  val activeNutritionMeal: MealEntity? = null,
  val activeOriginMeal: MealEntity? = null
)

class MealViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = MealRepository(application)

  private val _selectedDate = MutableStateFlow(LocalDate.now())
  val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

  private val _selectedMealFilter = MutableStateFlow<MealType?>(null)
  val selectedMealFilter: StateFlow<MealType?> = _selectedMealFilter.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  private val _isAllergyDialogOpen = MutableStateFlow(false)
  val isAllergyDialogOpen: StateFlow<Boolean> = _isAllergyDialogOpen.asStateFlow()

  private val _isDatePickerOpen = MutableStateFlow(false)
  val isDatePickerOpen: StateFlow<Boolean> = _isDatePickerOpen.asStateFlow()

  private val _activeNutritionMeal = MutableStateFlow<MealEntity?>(null)
  val activeNutritionMeal: StateFlow<MealEntity?> = _activeNutritionMeal.asStateFlow()

  private val _activeOriginMeal = MutableStateFlow<MealEntity?>(null)
  val activeOriginMeal: StateFlow<MealEntity?> = _activeOriginMeal.asStateFlow()

  // Room DB Flow for current date meals
  private val mealsFlow = _selectedDate.flatMapLatest { date ->
    repository.getMealsForDate(date)
  }

  // Room DB Flow for user allergy configuration
  private val userAllergiesFlow = repository.getSelectedAllergyIds()

  val uiState: StateFlow<MealScreenUiState> = combine(
    _selectedDate,
    _selectedMealFilter,
    mealsFlow,
    _isLoading,
    _errorMessage,
    userAllergiesFlow,
    _isAllergyDialogOpen,
    _isDatePickerOpen,
    _activeNutritionMeal,
    _activeOriginMeal
  ) { args ->
    val date = args[0] as LocalDate
    val filter = args[1] as MealType?
    @Suppress("UNCHECKED_CAST")
    val meals = args[2] as List<MealEntity>
    val loading = args[3] as Boolean
    val error = args[4] as String?
    @Suppress("UNCHECKED_CAST")
    val allergies = (args[5] as List<Int>).toSet()
    val allergyDialog = args[6] as Boolean
    val datePicker = args[7] as Boolean
    val nutritionMeal = args[8] as MealEntity?
    val originMeal = args[9] as MealEntity?

    val filteredMeals = if (filter == null) {
      meals
    } else {
      meals.filter { it.mealCode == filter.code }
    }

    MealScreenUiState(
      selectedDate = date,
      isToday = date == LocalDate.now(),
      selectedMealFilter = filter,
      meals = filteredMeals,
      isLoading = loading,
      errorMessage = error,
      userAllergyIds = allergies,
      isAllergyDialogOpen = allergyDialog,
      isDatePickerOpen = datePicker,
      activeNutritionMeal = nutritionMeal,
      activeOriginMeal = originMeal
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = MealScreenUiState()
  )

  init {
    // Initial fetch for today and current week
    loadMealsForCurrentWeek()
  }

  fun selectDate(date: LocalDate) {
    _selectedDate.value = date
    _errorMessage.value = null
    fetchMealForDate(date, force = false)
  }

  fun goToPreviousDay() {
    selectDate(_selectedDate.value.minusDays(1))
  }

  fun goToNextDay() {
    selectDate(_selectedDate.value.plusDays(1))
  }

  fun goToToday() {
    selectDate(LocalDate.now())
  }

  fun setMealFilter(filter: MealType?) {
    _selectedMealFilter.value = filter
  }

  fun refresh() {
    fetchMealForDate(_selectedDate.value, force = true)
  }

  private fun fetchMealForDate(date: LocalDate, force: Boolean = false) {
    viewModelScope.launch {
      _isLoading.value = true
      _errorMessage.value = null

      // Fetch the surrounding week to cache smoothly
      val startOfWeek = date.minusDays(date.dayOfWeek.value.toLong() - 1)
      val endOfWeek = startOfWeek.plusDays(6)

      val result = repository.fetchAndCacheMeals(
        startDate = startOfWeek,
        endDate = endOfWeek
      )

      _isLoading.value = false
      if (result.isFailure) {
        _errorMessage.value = "네트워크 확인 필요 (오프라인 캐시 표시 중)"
      }
    }
  }

  private fun loadMealsForCurrentWeek() {
    fetchMealForDate(_selectedDate.value, force = false)
  }

  fun toggleAllergy(allergenId: Int) {
    viewModelScope.launch {
      repository.toggleAllergy(allergenId)
    }
  }

  fun clearAllergies() {
    viewModelScope.launch {
      repository.clearAllergies()
    }
  }

  fun setAllergyDialogOpen(open: Boolean) {
    _isAllergyDialogOpen.value = open
  }

  fun setDatePickerOpen(open: Boolean) {
    _isDatePickerOpen.value = open
  }

  fun showNutrition(meal: MealEntity?) {
    _activeNutritionMeal.value = meal
  }

  fun showOrigin(meal: MealEntity?) {
    _activeOriginMeal.value = meal
  }
}
