package com.example.data.model

data class DishItem(
  val name: String,
  val rawText: String,
  val allergyIds: List<Int> = emptyList()
) {
  val allergies: List<Allergy> get() = allergyIds.mapNotNull { Allergy.getById(it) }

  companion object {
    fun fromRawLine(line: String): DishItem {
      val trimmed = line.trim()
      val cleanName = Allergy.cleanDishName(trimmed)
      val ids = Allergy.parseAllergyIds(trimmed)
      return DishItem(
        name = if (cleanName.isNotBlank()) cleanName else trimmed,
        rawText = trimmed,
        allergyIds = ids
      )
    }
  }
}
