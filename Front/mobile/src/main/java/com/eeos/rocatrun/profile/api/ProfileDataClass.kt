package com.eeos.rocatrun.profile.api


data class ProfileResponse(
    val status: Boolean,
    val message: String,
    val data: UserData
)

data class UserData(
    val nickname: String,
    val socialType: String,
    val height: Int,
    val weight: Int,
    val age: Int,
    val gender: String
)