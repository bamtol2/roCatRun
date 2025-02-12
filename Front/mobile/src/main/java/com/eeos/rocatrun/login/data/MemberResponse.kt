package com.eeos.rocatrun.login.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MemberResponse(
    val success: Boolean,
    val message : String,
    val data : MemberData?
) :Parcelable

@Parcelize
data class MemberData(
    val id: Int,
    val nickname: String,
    val level: Int,
    val experience: Long,
    val characterImage: String,
    val coin: Int
) :Parcelable