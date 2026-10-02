package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_allergies")
data class UserAllergyEntity(
  @PrimaryKey val allergenId: Int,
  val isSelected: Boolean = true
)
