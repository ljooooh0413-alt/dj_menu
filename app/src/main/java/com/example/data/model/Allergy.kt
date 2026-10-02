package com.example.data.model

/**
 * 나이스(NEIS) 학교급식 알레르기 유발 식품 표준 코드 (1~19번)
 */
data class Allergy(
  val id: Int,
  val name: String,
  val emoji: String,
  val description: String = ""
) {
  companion object {
    val ALLERGENS = listOf(
      Allergy(1, "난류", "🥚", "달걀, 메추리알 등 가금류의 알"),
      Allergy(2, "우유", "🥛", "우유, 유제품, 버터, 치즈 등"),
      Allergy(3, "메밀", "🌾", "메밀국수, 메밀전병 등"),
      Allergy(4, "땅콩", "🥜", "땅콩, 땅콩버터, 피넛버터 등"),
      Allergy(5, "대두", "🌱", "콩, 된장, 간장, 두부, 두유 등"),
      Allergy(6, "밀", "🍞", "밀가루, 빵, 면류, 과자류 등"),
      Allergy(7, "고등어", "🐟", "고등어 등 등푸른 생선"),
      Allergy(8, "게", "🦀", "게, 게살, 꽃게탕 등"),
      Allergy(9, "새우", "🦐", "새우, 새우젓, 건새우 등"),
      Allergy(10, "돼지고기", "🥓", "돼지고기, 햄, 소시지, 베이컨 등"),
      Allergy(11, "복숭아", "🍑", "복숭아, 통조림, 주스 등"),
      Allergy(12, "토마토", "🍅", "토마토, 케첩, 파스타소스 등"),
      Allergy(13, "아황산류", "🍷", "건조과일, 절임식품 등 식품첨가물"),
      Allergy(14, "호두", "🌰", "호두, 견과류 가공품"),
      Allergy(15, "닭고기", "🍗", "닭고기, 치킨, 삼계탕 등"),
      Allergy(16, "쇠고기", "🥩", "소고기, 사골, 쇠고기국 등"),
      Allergy(17, "오징어", "🦑", "오징어, 오징어채, 오징어볶음 등"),
      Allergy(18, "조개류", "🦪", "굴, 전복, 홍합, 바지락, 조개탕 등"),
      Allergy(19, "잣", "🌲", "잣, 견과류 토핑 등")
    )

    private val mapById = ALLERGENS.associateBy { it.id }

    fun getById(id: Int): Allergy? = mapById[id]

    /**
     * 요리명에서 알레르기 번호 파싱
     * 예: "쇠고기미역국5.6.16." -> [5, 6, 16]
     * 예: "돈육간장불고기 5.6.10." -> [5, 6, 10]
     */
    fun parseAllergyIds(text: String): List<Int> {
      val ids = mutableListOf<Int>()
      // 숫자들이 점(.)으로 구분되어 있는 패턴 추출
      val regex = Regex("""(?:\s*\(?[0-9]+(?:\.[0-9]+)*\.?\)?)""")
      val matches = regex.findAll(text)
      for (match in matches) {
        val numberRegex = Regex("""\d+""")
        numberRegex.findAll(match.value).forEach { numMatch ->
          val num = numMatch.value.toIntOrNull()
          if (num != null && num in 1..19 && !ids.contains(num)) {
            ids.add(num)
          }
        }
      }
      return ids.sorted()
    }

    /**
     * 요리명에서 알레르기 표시 번호 및 특수 기호를 제거하여 순수 요리명 추출
     * 예: "쇠고기미역국5.6.16." -> "쇠고기미역국"
     * 예: "수제치킨커틀렛 (대진) 1.2.5.6.15." -> "수제치킨커틀렛 (대진)"
     */
    fun cleanDishName(text: String): String {
      return text
        .replace(Regex("""(?:\s*\(?[0-9]+(?:\.[0-9]+)*\.?\)?)"""), "")
        .replace(Regex("""[*#@]+"""), "")
        .trim()
    }
  }
}
