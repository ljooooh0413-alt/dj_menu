package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.MealEntity
import com.example.data.model.UserAllergyEntity

@Database(
  entities = [MealEntity::class, UserAllergyEntity::class],
  version = 1,
  exportSchema = false
)
abstract class MealDatabase : RoomDatabase() {
  abstract fun mealDao(): MealDao
  abstract fun userAllergyDao(): UserAllergyDao

  companion object {
    @Volatile
    private var INSTANCE: MealDatabase? = null

    fun getInstance(context: Context): MealDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          MealDatabase::class.java,
          "daejin_meal_db"
        ).fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
