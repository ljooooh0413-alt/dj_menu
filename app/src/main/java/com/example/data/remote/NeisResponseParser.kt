package com.example.data.remote

import com.example.data.model.MealEntity
import org.json.JSONObject

object NeisResponseParser {

  fun parseMealEntities(jsonString: String): List<MealEntity> {
    val results = mutableListOf<MealEntity>()
    if (jsonString.isBlank()) return emptyList()

    try {
      val root = JSONObject(jsonString)

      // NEIS API empty/error result: {"RESULT":{"CODE":"INFO-200","MESSAGE":"해당하는 데이터가 없습니다."}}
      if (root.has("RESULT")) {
        return emptyList()
      }

      if (!root.has("mealServiceDietInfo")) {
        return emptyList()
      }

      val array = root.getJSONArray("mealServiceDietInfo")
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        if (obj.has("row")) {
          val rowArray = obj.getJSONArray("row")
          for (j in 0 until rowArray.length()) {
            val row = rowArray.getJSONObject(j)
            val date = row.optString("MLSV_YMD", "").trim()
            val mealCode = row.optString("MMEAL_SC_CODE", "2").trim()
            val mealName = row.optString("MMEAL_SC_NM", "중식").trim()
            val rawMenu = row.optString("DDISH_NM", "").trim()
            val rawCalorie = row.optString("CAL_INFO", "").trim()
            val rawNutrition = row.optString("NTR_INFO", "").trim()
            val rawOrigin = row.optString("ORPLC_INFO", "").trim()
            val schoolName = row.optString("SCHUL_NM", "대진전자통신고등학교").trim()

            if (date.isNotBlank() && rawMenu.isNotBlank()) {
              results.add(
                MealEntity(
                  date = date,
                  mealCode = mealCode,
                  mealName = mealName,
                  rawMenu = rawMenu,
                  rawCalorie = rawCalorie,
                  rawNutrition = rawNutrition,
                  rawOrigin = rawOrigin,
                  schoolName = schoolName,
                  lastUpdated = System.currentTimeMillis()
                )
              )
            }
          }
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return results
  }
}
