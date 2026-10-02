package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.UserAllergyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAllergyDao {
  @Query("SELECT allergenId FROM user_allergies WHERE isSelected = 1")
  fun getSelectedAllergenIds(): Flow<List<Int>>

  @Query("SELECT allergenId FROM user_allergies WHERE isSelected = 1")
  suspend fun getSelectedAllergenIdsDirect(): List<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun setAllergy(entity: UserAllergyEntity)

  @Query("DELETE FROM user_allergies WHERE allergenId = :allergenId")
  suspend fun removeAllergy(allergenId: Int)

  @Query("DELETE FROM user_allergies")
  suspend fun clearAll()
}
