package com.eeos.rocatrun.login.social

data class LoginResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val message: String?
)
