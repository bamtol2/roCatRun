package com.eeos.rocatrun.login.data
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NicknameCheckResponse(
    val success : Boolean,
    val message: String,
    val data: Boolean
): Parcelable
