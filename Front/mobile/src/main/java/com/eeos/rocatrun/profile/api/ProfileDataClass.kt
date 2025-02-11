package com.eeos.rocatrun.profile.api


// 마이페이지 정보 조회, 로그아웃
data class ProfileResponse(
    val status: Boolean,
    val message: String,
    val data: UserData?
)

data class UserData(
    val nickname: String,
    val socialType: String,
    val height: Int,
    val weight: Int,
    val age: Int,
    val gender: String
)

// 닉네임 중복 확인 응답
data class NicknameCheckResponse(
    val success: Boolean,
    val message: String,
    val data: Boolean
)