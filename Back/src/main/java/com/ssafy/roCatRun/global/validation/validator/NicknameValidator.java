package com.ssafy.roCatRun.global.validation.validator;

import com.ssafy.roCatRun.global.validation.annotation.ValidNickname;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;
import java.util.Set;

public class NicknameValidator implements ConstraintValidator<ValidNickname, String> {

    private static final Pattern ENGLISH_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    private static final Pattern KOREAN_PATTERN = Pattern.compile("^[가-힣0-9]+$");
    private static final Pattern NUMBERS_ONLY = Pattern.compile("^[0-9]+$");
    private static final Pattern WHITESPACE = Pattern.compile("\\s");

    private static final Set<String> BANNED_WORDS = Set.of(
            // 비속어
            "ㅅㅂ", "시발", "씨발", "tq", "병신", "ㅂㅅ", "븅신", "빙신", "ㅄ", "ㅂㅅㅣ",
            "개새", "개세", "개소리", "개년", "개놈", "개넘",
            // 성적 비속어
            "졸라", "존나", "ㅈㄴ", "지랄", "ㅈㄹ", "씹", "ㅆㅂ",
            // 부모 관련
            "니미", "느금", "엄창", "mbw", "nmw",
            // 사회적 차별 용어
            "장애", "찐따", "흑인", "짱깨", "짱께",
            // 우회 표현
            "시1발", "s1bal", "tlqkf", "병1신", "ㅡㅡ"
    );

    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext context) {
        if (nickname == null || nickname.trim().isEmpty()) {
            addConstraintViolation(context, "닉네임은 필수입니다.");
            return false;
        }

        // 숫자로만 이루어진 경우 체크
        if (NUMBERS_ONLY.matcher(nickname).matches()) {
            addConstraintViolation(context, "닉네임은 숫자로만 구성될 수 없습니다.");
            return false;
        }

        // 영어 닉네임 체크
        if (ENGLISH_PATTERN.matcher(nickname).matches()) {
            if (nickname.length() < 2 || nickname.length() > 8) {
                addConstraintViolation(context, "영어 닉네임은 2-8자여야 합니다.");
                return false;
            }
        }
        // 한글 닉네임 체크
        else if (KOREAN_PATTERN.matcher(nickname).matches()) {
            if (nickname.length() < 2 || nickname.length() > 6) {
                addConstraintViolation(context, "한글 닉네임은 2-6자여야 합니다.");
                return false;
            }
        }
        // 특수문자가 포함된 경우
        else {
            addConstraintViolation(context, "닉네임은 한글, 영문, 숫자만 사용 가능합니다.");
            return false;
        }

        // 비속어 체크
        if (containsBannedWord(nickname)) {
            addConstraintViolation(context, "부적절한 단어가 포함되어 있습니다.");
            return false;
        }

        return true;
    }

    private boolean containsBannedWord(String nickname) {
        String lowerNickname = nickname.toLowerCase();
        return BANNED_WORDS.stream().anyMatch(lowerNickname::contains);
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}