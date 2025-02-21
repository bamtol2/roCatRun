package com.eeos.rocatrun.login.util

object NicknameValidator {
    private val ENGLISH_REGEX = Regex("^[a-zA-Z0-9]+$")
    private val KOREAN_REGEX = Regex("^[가-힣0-9]+$")
    private val NUMBERS_ONLY = Regex("^[0-9]+$")
    private val BANNED_WORDS = setOf(
        "ㅅㅂ", "시발", "씨발", "tq", "병신", "ㅂㅅ", "븅신", "빙신", "ㅄ", "ㅂㅅㅣ",
        "개새", "개세", "개소리", "개년", "개놈", "개넘",
        "졸라", "존나", "ㅈㄴ", "지랄", "ㅈㄹ", "씹", "ㅆㅂ",
        "니미", "느금", "엄창", "mbw", "nmw",
        "장애", "찐따", "흑인", "짱깨", "짱께",
        "시1발", "s1bal", "tlqkf", "병1신", "ㅡㅡ"
    )

    fun validate(nickname: String): Pair<Boolean, String> {
        // 1) 빈 값 체크
        if (nickname.isBlank()) {
            return false to "닉네임은 필수입니다."
        }

        // 2) 숫자만으로 구성
        if (nickname.matches(NUMBERS_ONLY)) {
            return false to "닉네임은 숫자로만 구성될 수 없습니다."
        }

        // 3) 영어 닉네임
        if (nickname.matches(ENGLISH_REGEX)) {
            if (nickname.length < 2 || nickname.length > 8) {
                return false to "영어 닉네임은 2~8자여야 합니다."
            }
        }
        // 4) 한글 닉네임
        else if (nickname.matches(KOREAN_REGEX)) {
            if (nickname.length < 2 || nickname.length > 6) {
                return false to "한글 닉네임은 2~6자여야 합니다."
            }
        }
        // 5) 그 외 (특수문자, 공백 등)
        else {
            return false to "닉네임은 한글, 영문, 숫자만 사용 가능합니다."
        }

        // 6) 금칙어(비속어) 체크
        val lowerNickname = nickname.lowercase()
        if (BANNED_WORDS.any { lowerNickname.contains(it) }) {
            return false to "부적절한 단어가 포함되어 있습니다."
        }

        // 모두 통과
        return true to ""
    }
}
