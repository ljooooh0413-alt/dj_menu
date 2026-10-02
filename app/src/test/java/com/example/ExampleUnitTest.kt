package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testAllergyParsing() {
    val sampleDish = "쇠고기미역국5.6.16."
    val cleanName = com.example.data.model.Allergy.cleanDishName(sampleDish)
    val allergies = com.example.data.model.Allergy.parseAllergyIds(sampleDish)

    assertEquals("쇠고기미역국", cleanName)
    assertEquals(listOf(5, 6, 16), allergies)
  }

}
