package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
  @Query("SELECT * FROM meals WHERE date = :date ORDER BY mealCode ASC")
  fun getMealsByDate(date: String): Flow<List<MealEntity>>

  @Query("SELECT * FROM meals WHERE date = :date ORDER BY mealCode ASC")
  suspend fun getMealsByDateDirect(date: String): List<MealEntity>

  @Query("SELECT * FROM meals WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, mealCode ASC")
  fun getMealsBetween(startDate: String, endDate: String): Flow<List<MealEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMeals(meals: List<MealEntity>)

  @Query("DELETE FROM meals WHERE date = :date")
  suspend fun deleteMealsByDate(date: String)

  @Query("DELETE FROM meals")
  suspend fun clearAll()
}
