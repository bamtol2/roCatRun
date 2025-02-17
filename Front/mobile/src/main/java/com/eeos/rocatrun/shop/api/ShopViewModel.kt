package com.eeos.rocatrun.shop.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eeos.rocatrun.api.RetrofitInstance
import com.eeos.rocatrun.closet.api.InventoryItem
import kotlinx.coroutines.launch

class ShopViewModel : ViewModel() {

    // 전체 인벤토리 목록 데이터
    private val _allInventoryList = mutableStateOf<List<InventoryItem>>(emptyList())
    val allInventoryList: State<List<InventoryItem>> = _allInventoryList

    // 선택된 아이템 id 리스트
    private val _selectedItems = mutableStateOf<List<Int>>(emptyList())
    val selectedItems: State<List<Int>> = _selectedItems

    // 총 금액
    private val _totalPrice = mutableIntStateOf(0)
    val totalPrice: State<Int> = _totalPrice

    // 아이템 선택 여부 및 금액 관리
    fun toggleItemSelection(itemId: Int, price: Int, equipped: Boolean, context: Context) {
        if (equipped) {
            Toast.makeText(context, "장착 중인 아이템은 판매할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (_selectedItems.value.contains(itemId)) {
            _selectedItems.value = _selectedItems.value.filter { it != itemId }
            _totalPrice.value -= price
        } else {
            _selectedItems.value += itemId
            _totalPrice.value += price
        }
    }

    // 판매 완료 응답 데이터
    private val _sellResponse = MutableLiveData<SellItemsResponse>()
    val sellResponse: LiveData<SellItemsResponse> = _sellResponse

    private val retrofitInstance = RetrofitInstance.getInstance().create(ShopAPI::class.java)

    // 전체 인벤토리 조회
    fun fetchAllInventoryShop(auth: String?) {
        if (auth == null) {
            Log.d("debug", "토큰이 없습니다.")
            return
        }

        Log.d("api", "(shop) 전체 인벤토리 목록 호출")
        viewModelScope.launch {
            try {
                val response = retrofitInstance.getAllInventoryShop(token = "Bearer $auth",)

                if (response.isSuccessful) {
                    response.body()?.let { inventoryResponse ->
                        _allInventoryList.value = inventoryResponse.data
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


    // 아이템 판매
    fun postSellItem(auth: String?, inventoryIds: List<Int>, totalPrice: Int) {
        if (auth == null) {
            Log.d("debug", "토큰이 없습니다.")
            return
        }

        Log.d("api", "아이템 판매 호출")
        viewModelScope.launch {
            try {
                val sellRequest = SellItemsRequest(inventoryIds, totalPrice)
                val response = retrofitInstance.sellItems(token = "Bearer $auth", request = sellRequest)

                if (response.isSuccessful) {
                    _sellResponse.value = response.body()
                    _selectedItems.value = emptyList()
                    _totalPrice.intValue = 0
                    fetchAllInventoryShop(auth)
                    Log.d("api", "아이템 판매 성공")
                } else {
                    Log.e("api", "아이템 판매 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("api", "API 호출 중 오류 발생: ${e.localizedMessage}")
            }
        }
    }
}