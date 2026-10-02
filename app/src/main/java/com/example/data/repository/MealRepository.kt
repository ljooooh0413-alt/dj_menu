package com.example.data.repository

import android.content.Context
import com.example.data.local.MealDatabase
import com.example.data.model.MealEntity
import com.example.data.model.UserAllergyEntity
import com.example.data.remote.NeisApiService
import com.example.data.remote.NeisResponseParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MealRepository(context: Context) {

  private val database = MealDatabase.getInstance(context)
  private val mealDao = database.mealDao()
  private val userAllergyDao = database.userAllergyDao()
  private val apiService = NeisApiService.create()

  private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

  fun getMealsForDate(date: LocalDate): Flow<List<MealEntity>> {
    val dateStr = date.format(dateFormatter)
    return mealDao.getMealsByDate(dateStr)
  }

  suspend fun fetchAndCacheMeals(
    startDate: LocalDate,
    endDate: LocalDate = startDate
  ): Result<List<MealEntity>> = withContext(Dispatchers.IO) {
    try {
      val fromStr = startDate.format(dateFormatter)
      val toStr = endDate.format(dateFormatter)

      val response = if (fromStr == toStr) {
        apiService.getMealInfo(mealDate = fromStr)
      } else {
        apiService.getMealInfo(fromDate = fromStr, toDate = toStr)
      }

      if (response.isSuccessful) {
        val bodyString = response.body()?.string().orEmpty()
        val parsedMeals = NeisResponseParser.parseMealEntities(bodyString)
        if (parsedMeals.isNotEmpty()) {
          mealDao.insertMeals(parsedMeals)
        }
        Result.success(parsedMeals)
      } else {
        Result.failure(Exception("NEIS API 요청 실패 (HTTP ${response.code()})"))
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  fun getSelectedAllergyIds(): Flow<List<Int>> {
    return userAllergyDao.getSelectedAllergenIds()
  }

  suspend fun toggleAllergy(allergenId: Int) = withContext(Dispatchers.IO) {
    val currentSelected = userAllergyDao.getSelectedAllergenIdsDirect()
    if (currentSelected.contains(allergenId)) {
      userAllergyDao.removeAllergy(allergenId)
    } else {
      userAllergyDao.setAllergy(UserAllergyEntity(allergenId = allergenId, isSelected = true))
    }
  }

  suspend fun clearAllergies() = withContext(Dispatchers.IO) {
    userAllergyDao.clearAll()
  }

  companion object {
    const val SCHOOL_NAME = "대진전자통신고등학교"
    const val OFFICE_NAME = "부산광역시교육청"
    const val SCHOOL_ADDRESS = "부산광역시 금정구 수림로 92"
    const val SCHOOL_PHONE = "051-582-8100"
    const val SCHOOL_CODE = "7150597"
    const val OFFICE_CODE = "C10"
  }
}
