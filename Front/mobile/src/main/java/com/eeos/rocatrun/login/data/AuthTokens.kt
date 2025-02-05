package com.eeos.rocatrun.login.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthTokens(
    val data: NewTokenData?
) : Parcelable

@Parcelize
data class NewTokenData(
    val accessToken: String?,
    val refreshToken: String?
) : Parcelable