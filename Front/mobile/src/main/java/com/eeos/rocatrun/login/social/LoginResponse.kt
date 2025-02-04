package com.eeos.rocatrun.login.social

import java.io.Serializable
data class LoginResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val message: String?
) : Serializable
