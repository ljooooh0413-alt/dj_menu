package com.example.data.model

enum class MealType(
  val code: String,
  val displayName: String,
  val emoji: String,
  val timeDesc: String
) {
  BREAKFAST("1", "조식", "🌅", "07:30 ~ 08:30"),
  LUNCH("2", "중식", "☀️", "12:30 ~ 13:30"),
  DINNER("3", "석식", "🌙", "18:00 ~ 19:00");

  companion object {
    fun fromCode(code: String?): MealType {
      return entries.find { it.code == code } ?: LUNCH
    }

    fun fromName(name: String?): MealType {
      return entries.find { it.displayName == name?.trim() } ?: LUNCH
    }
  }
}
