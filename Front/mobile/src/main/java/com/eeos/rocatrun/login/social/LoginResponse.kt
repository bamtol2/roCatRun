package com.eeos.rocatrun.login.social

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class LoginResponse(
    val data: TokenData?
) : Parcelable

@Parcelize
data class TokenData(
    val token: Token?
) : Parcelable

@Parcelize
data class Token(
    val accessToken: String?,
    val refreshToken: String?
) : Parcelable
