package com.example.data.model

import androidx.room.Entity

@Entity(
  tableName = "meals",
  primaryKeys = ["date", "mealCode"]
)
data class MealEntity(
  val date: String,             // YYYYMMDD (e.g. "20261002")
  val mealCode: String,         // "1", "2", "3"
  val mealName: String,         // "조식", "중식", "석식"
  val rawMenu: String,          // <br/> separated dish names
  val rawCalorie: String,       // e.g. "754.2 Kcal"
  val rawNutrition: String,     // <br/> separated nutritional stats
  val rawOrigin: String,        // <br/> separated origin stats
  val schoolName: String = "대진전자통신고등학교",
  val lastUpdated: Long = System.currentTimeMillis()
) {
  val mealType: MealType get() = MealType.fromCode(mealCode)

  val dishes: List<DishItem> get() {
    return rawMenu
      .split("<br/>", "<br>", "\n")
      .map { it.trim() }
      .filter { it.isNotBlank() }
      .map { DishItem.fromRawLine(it) }
  }

  val calorieText: String get() {
    return if (rawCalorie.isNotBlank()) rawCalorie.trim() else "정보 없음"
  }

  val nutritionList: List<String> get() {
    return rawNutrition
      .split("<br/>", "<br>", "\n")
      .map { it.trim() }
      .filter { it.isNotBlank() }
  }

  val originList: List<String> get() {
    return rawOrigin
      .split("<br/>", "<br>", "\n")
      .map { it.trim() }
      .filter { it.isNotBlank() }
  }

  val allAllergenIds: Set<Int> get() {
    return dishes.flatMap { it.allergyIds }.toSet()
  }
}
