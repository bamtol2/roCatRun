package com.eeos.rocatrun.closet.api

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import com.eeos.rocatrun.api.RetrofitInstance
import androidx.compose.runtime.State


class ClosetViewModel : ViewModel() {
    // 전송 완료 응답 데이터
    private val _sendImageResult = MutableLiveData<UploadResponse>()
    val sendImageResult: LiveData<UploadResponse> = _sendImageResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // 인벤토리 목록 데이터
    private val _inventoryList = mutableStateOf<List<InventoryItem>>(emptyList())
    val inventoryList: State<List<InventoryItem>> = _inventoryList

    private val _isItemLoading = MutableLiveData<Boolean>()
    val isItemLoading: LiveData<Boolean> = _isItemLoading

    // 장착된 아이템 id 리스트
    private val _equippedItems = mutableStateOf<List<Int>>(emptyList())
    val equippedItems: State<List<Int>> = _equippedItems

    // 아이템 목록 데이터
    private val _itemList = mutableStateOf<List<Item>>(emptyList())
    val itemList: State<List<Item>> = _itemList

    // 아이템 상태 변경 함수
    fun toggleItemEquipped(clickedItem: InventoryItem) {
        _inventoryList.value = _inventoryList.value.map {
            if (it.inventoryId == clickedItem.inventoryId) {
                it.copy(equipped = !it.equipped)
            } else if (it.category == clickedItem.category) {
                it.copy(equipped = false)
            } else {
                it
            }
        }
        // 장착된 아이템 리스트 업데이트
        _equippedItems.value = _inventoryList.value
            .filter { it.equipped }
            .map { it.inventoryId }
    }

    private fun initializeItemList(items: List<InventoryItem>) {
        _inventoryList.value = items
        _equippedItems.value = items.filter { it.equipped }.map { it.inventoryId }
    }


    private val retrofitInstance = RetrofitInstance.getInstance().create(ClosetAPI::class.java)

    // 이미지 전송
    fun sendCharacterImage(auth: String?, filePath: String) {
        if (auth == null) {
            Log.d("debug", "토큰이 없습니다.")
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true

                // 파일 생성
                val file = File(filePath)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData(
                    "image",
                    file.name,
                    requestFile
                )

                // API 호출
                val response = retrofitInstance.uploadImage(
                    token = "Bearer $auth",
                    image = imagePart
                )

                if (response.isSuccessful) {
                    _sendImageResult.value = response.body()
                    Log.d("api", "업로드 성공")
                } else {
                    Log.e("api", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("api", "Exception: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 전체 인벤토리 조회 (중복 제거)
    fun fetchAllInventory(auth: String?) {
        if (auth == null) {
            Log.d("debug", "토큰이 없습니다.")
            return
        }

        _isItemLoading.value = true
        Log.d("api", "인벤토리 목록 조회 호출")
        viewModelScope.launch {
            try {
                val response = retrofitInstance.getAllInventory(token = "Bearer $auth",)

                if (response.isSuccessful) {
                    response.body()?.let { inventoryResponse ->
                        _inventoryList.value = inventoryResponse.data
                        initializeItemList(inventoryResponse.data)
                        Log.d("api", "아이템 조회 성공")
                    }
                } else {
                    Log.d("api", "응답 실패: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.d("api", "예외 발생: ${e.message}")
            } finally {
                _isItemLoading.value = false
            }
        }
    }

    // 아이템 착용 상태 변경
    fun changeEquipStatus(auth: String?, inventoryIds: List<Int>) {
        if (auth == null) {
            Log.d("debug", "토큰이 없습니다.")
            return
        }

        Log.d("api", "아이템 착용 상태 변경 호출")
        viewModelScope.launch {
            try {
                // EquipRequest 생성
                val equipRequest = EquipRequest(inventoryIds)
                val response = retrofitInstance.putItemsEquip(token = "Bearer $auth", requestBody = equipRequest)

                if (response.isSuccessful) {
                    response.body()?.let { inventoryResponse ->
                        _inventoryList.value = inventoryResponse.data
                        initializeItemList(inventoryResponse.data)
                    }
                    Log.d("api", "아이템 착용 상태 변경 성공")
                } else {
                    Log.e("api", "아이템 착용 상태 변경 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("api", "API 호출 중 오류 발생: ${e.localizedMessage}")
            }
        }
    }

    // 전체 아이템 조회
    fun fetchAllItem(auth: String?) {
        if (auth == null) {
            Log.d("debug", "토큰이 없습니다.")
            return
        }

        Log.d("api", "아이템 목록 조회 호출")
        viewModelScope.launch {
            try {
                val response = retrofitInstance.getAllItems(token = "Bearer $auth",)

                if (response.isSuccessful) {
                    response.body()?.let { itemResponse ->
                        _itemList.value = itemResponse.data
                        Log.d("api", "아이템 조회 성공")
                    }
                } else {
                    Log.d("api", "응답 실패: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.d("api", "예외 발생: ${e.message}")
            } finally {
            }
        }
    }

}