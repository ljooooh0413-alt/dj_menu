package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Allergy
import com.example.data.model.DishItem
import com.example.data.model.MealEntity
import com.example.ui.theme.AllergyAlertRed
import com.example.ui.theme.AllergyAlertRedBg

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MealCard(
  meal: MealEntity,
  userAllergyIds: Set<Int>,
  onViewNutrition: () -> Unit,
  onViewOrigin: () -> Unit,
  modifier: Modifier = Modifier
) {
  val mealType = meal.mealType
  val dishes = meal.dishes

  // Check if any dish in this meal matches the user's allergy preferences
  val matchedAllergenIds = meal.allAllergenIds.intersect(userAllergyIds)
  val hasAllergyWarning = matchedAllergenIds.isNotEmpty()

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("meal_card_${meal.mealCode}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      // Header: Meal Name & Calories
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = mealType.emoji,
              fontSize = 22.sp
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = meal.mealName,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = mealType.timeDesc,
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.secondary,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = meal.schoolName,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Calorie Badge
        if (meal.rawCalorie.isNotBlank()) {
          Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
            shape = RoundedCornerShape(12.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = meal.rawCalorie,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }
        }
      }

      // User Allergy Warning Banner (if matching allergens detected)
      if (hasAllergyWarning) {
        val warningNames = matchedAllergenIds
          .mapNotNull { Allergy.getById(it)?.name }
          .joinToString(", ")

        Spacer(modifier = Modifier.height(14.dp))
        Surface(
          color = AllergyAlertRedBg,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = "알레르기 주의",
              tint = AllergyAlertRed,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "주의: 등록하신 알레르기 식품($warningNames)이 포함되어 있습니다.",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
              color = AllergyAlertRed
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
      Spacer(modifier = Modifier.height(14.dp))

      // Dish Items List
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        dishes.forEach { dish ->
          DishRow(
            dish = dish,
            userAllergyIds = userAllergyIds
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Bottom Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onViewNutrition,
          modifier = Modifier
            .weight(1f)
            .height(44.dp)
            .testTag("nutrition_button_${meal.mealCode}"),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.PieChart,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "영양 정보",
            style = MaterialTheme.typography.labelMedium
          )
        }

        OutlinedButton(
          onClick = onViewOrigin,
          modifier = Modifier
            .weight(1f)
            .height(44.dp)
            .testTag("origin_button_${meal.mealCode}"),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "원산지 정보",
            style = MaterialTheme.typography.labelMedium
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DishRow(
  dish: DishItem,
  userAllergyIds: Set<Int>,
  modifier: Modifier = Modifier
) {
  val containsUserAllergy = dish.allergyIds.any { userAllergyIds.contains(it) }

  Row(
    modifier = modifier.fillMaxWidth(),
    verticalAlignment = Alignment.Top
  ) {
    // Bullet
    Box(
      modifier = Modifier
        .padding(top = 7.dp)
        .size(6.dp)
        .clip(CircleShape)
        .background(
          if (containsUserAllergy) AllergyAlertRed
          else MaterialTheme.colorScheme.primary
        )
    )

    Spacer(modifier = Modifier.width(10.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = dish.name,
          style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = if (containsUserAllergy) FontWeight.Bold else FontWeight.Medium
          ),
          color = if (containsUserAllergy) AllergyAlertRed else MaterialTheme.colorScheme.onSurface
        )

        if (containsUserAllergy) {
          Surface(
            color = AllergyAlertRedBg,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.padding(start = 6.dp)
          ) {
            Text(
              text = "주의",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = AllergyAlertRed,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      // Allergen tag chips
      if (dish.allergies.isNotEmpty()) {
        Spacer(modifier = Modifier.height(4.dp))
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          dish.allergies.forEach { allergy ->
            val isUserAllergen = userAllergyIds.contains(allergy.id)
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (isUserAllergen) AllergyAlertRedBg else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.border(
                width = 0.5.dp,
                color = if (isUserAllergen) AllergyAlertRed.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
              )
            ) {
              Text(
                text = "${allergy.name} (${allergy.id})",
                fontSize = 10.sp,
                fontWeight = if (isUserAllergen) FontWeight.Bold else FontWeight.Normal,
                color = if (isUserAllergen) AllergyAlertRed else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }
          }
        }
      }
    }
  }
}
