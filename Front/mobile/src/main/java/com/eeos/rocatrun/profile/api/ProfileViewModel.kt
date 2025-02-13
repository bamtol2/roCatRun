package com.eeos.rocatrun.profile.api

import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eeos.rocatrun.login.data.TokenStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileViewModel : ViewModel() {

    // 마이프로필 데이터
    private val _profileData = MutableLiveData<ProfileResponse>()
    val profileData: LiveData<ProfileResponse> = _profileData

    // 닉네임 중복 확인 데이터
    private val _nicknameCheckResult = MutableLiveData<Boolean>()
    val nicknameCheckResult: LiveData<Boolean> = _nicknameCheckResult

    // 마이프로필 수정 데이터
    private val _updateProfileResponse = MutableLiveData<UpdateProfileResponse>()
    val updateProfileResponse: LiveData<UpdateProfileResponse> = _updateProfileResponse

    private val retrofitInstance = RetrofitInstance.getInstance().create(ProfileAPI::class.java)

    // 회원 정보 조회
    fun fetchProfileInfo(auth: String?) {
        if (auth != null) {
            Log.d("api", "마이페이지 호출 시작")
            retrofitInstance.getProfileInfo("Bearer $auth")
                .enqueue(object : Callback<ProfileResponse> {
                    override fun onResponse(
                        call: Call<ProfileResponse>,
                        response: Response<ProfileResponse>
                    ) {
                        if (response.isSuccessful) {
                            _profileData.value = response.body()
                            Log.d("api", "정보 조회 성공")
                        } else {
                            println("Error: ${response.errorBody()}")
                            Log.d("api", response.toString())
                        }
                    }

                    override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                        Log.d("api", "Error: ${t.localizedMessage}")
                    }
                })
        } else {
            Log.d("debug", "토큰이 없습니다.")
        }
    }

    // 닉네임 중복 확인
    fun checkNicknameAvailability(auth: String?, nickname: String) {
        if (auth != null) {
            Log.d("api", "중복 확인 호출 시작")
            retrofitInstance.checkNickname("Bearer $auth", nickname)
                .enqueue(object : Callback<NicknameCheckResponse> {
                    override fun onResponse(
                        call: Call<NicknameCheckResponse>,
                        response: Response<NicknameCheckResponse>
                    ) {
                        if (response.isSuccessful) {
                            _nicknameCheckResult.value = !(response.body()?.data ?: false)
                            Log.d("api", _nicknameCheckResult.value.toString())
                        } else {
                            _nicknameCheckResult.value = false
                            Log.d("api", "Error: ${response.errorBody()}")
                        }
                    }

                    override fun onFailure(call: Call<NicknameCheckResponse>, t: Throwable) {
                        _nicknameCheckResult.value = false
                        Log.d("api", "Error: ${t.localizedMessage}")
                    }
                })
        } else {
            Log.d("debug", "토큰이 없습니다.")
        }
    }

    // 마이프로필 수정
    fun updateProfile(auth: String?, profileRequest: UpdateProfileRequest) {
        if (auth != null) {
            Log.d("api", "중복 확인 호출 시작")
            retrofitInstance.updateProfile("Bearer $auth", profileRequest)
                .enqueue(object : Callback<UpdateProfileResponse> {
                    override fun onResponse(
                        call: Call<UpdateProfileResponse>,
                        response: Response<UpdateProfileResponse>
                    ) {
                        if (response.isSuccessful) {
                            _updateProfileResponse.value = response.body()
                            Log.d("api", _updateProfileResponse.value.toString())
                        } else {
                            Log.e("api", "Error: ${response.errorBody()}")
                        }
                    }

                    override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                        Log.e("api", "Failure: ${t.localizedMessage}")
                    }
                })
        } else {
            Log.d("debug", "토큰이 없습니다.")
        }
    }

    // 로그아웃
    fun fetchLogout(auth: String?) {
        if (auth != null) {
            Log.d("api", "로그아웃 호출 시작")
            retrofitInstance.userLogout("Bearer $auth").enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("api", "로그아웃 성공")
                    } else {
                        println("Error: ${response.errorBody()}")
                        Log.d("api", response.toString())
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.d("api", t.localizedMessage)
                }
            })
        } else {
            Log.d("debug", "토큰이 없습니다.")
        }
    }
    // 회원 탈퇴
    fun deleteMember(auth: String?){
        if (auth != null){
            retrofitInstance.memberDelete("Bearer $auth")
                .enqueue(object : Callback<DeleteMemberResponse>{
                    override fun onResponse(
                        call: Call<DeleteMemberResponse>,
                        response: Response<DeleteMemberResponse>
                    ){
                        if (response.isSuccessful){
                            Log.d("api", "회원 탈퇴 성공: ${response.body()?.message}")
                        }else {
                            Log.e("api", "회원 탈퇴 실패: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<DeleteMemberResponse>, t: Throwable) {
                        Log.e("api", "회원 탈퇴 호출 실패: ${t.localizedMessage}")
                    }
                }



                )
        }else {
            Log.d("debug", "토큰이 없습니다.")
        }
    }
}