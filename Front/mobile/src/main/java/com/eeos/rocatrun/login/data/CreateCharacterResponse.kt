package com.eeos.rocatrun.login.data
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateCharacterResponse(
    val success: Boolean,
    val message: String
): Parcelable