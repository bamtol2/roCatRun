package com.eeos.rocatrun.viewmodel

import android.content.Context
import android.provider.ContactsContract.Data
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable

data class UserData(
    val nickname: String,
    val distance: Double,
    val itemCount: Int
)
// ViewModel 정의
class MultiUserViewModel(context: Context) : ViewModel(), DataClient.OnDataChangedListener {

    private lateinit var dataClient: DataClient
    private val _userList = MutableStateFlow<List<UserData>>(emptyList())

    private var playersData by mutableStateOf<PlayersData?>(null)
    // 실시간으로 받아올 사용자들의 러닝 데이터


    // 데이터 클래스 정의

    // 실시간 유저 러닝 데이터
    data class PlayersData(
        val nickname: String,
        val distance: Double,
        val itemCount: Int
    )



    val userList: StateFlow<List<UserData>> get() = _userList

    // 테스트용 데이터 주기적 업데이트
    init {
        viewModelScope.launch {
            dataClient = Wearable.getDataClient(context)
            dataClient.addListener(this@MultiUserViewModel)
            while (true) {
                delay(1000)  // 1초마다 데이터 갱신
                val updatedList = generateMockData()
                _userList.emit(updatedList)
            }
        }
    }


    override fun onDataChanged(dataEvents: DataEventBuffer){
        dataEvents.forEach{ event ->
            if(event.type == DataEvent.TYPE_CHANGED){
                val dataItem = event.dataItem
                when(dataItem.uri.path){
                    "/players_data" -> processPlayersData(dataItem)
                }
            }

        }
    }
    private fun processPlayersData(dataItem: DataItem) {
        val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
        val nickname = dataMap.getString("userId", "Unknown")
        val distance = dataMap.getDouble("distance", 0.0)
        val itemCount = dataMap.getInt("itemUseCount", 0)

        // playersData 업데이트
        val updatedData = PlayersData(nickname, distance, itemCount)

        // 사용자 리스트 업데이트 (예시로 단순히 리스트에 추가하는 방식)
        viewModelScope.launch {
            val currentList = _userList.value.toMutableList()
            currentList.removeIf { it.nickname == nickname }  // 기존 데이터 제거
            currentList.add(UserData(updatedData.nickname, updatedData.distance, updatedData.itemCount))
            _userList.emit(currentList)
        }
    }


    private fun generateMockData(): List<UserData> {
        val users = listOf("마이애미", "과즙가람", "타노스")
        return users.map {
            UserData(
                nickname = it,
                distance = Random.nextDouble(4.0, 5.0),
                itemCount = Random.nextInt(1, 5)
            )
        }
    }
}

// 사용자 정보를 표시하는 카드 컴포저블
@Composable
fun UserInfoCard(user: UserData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.DarkGray)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = user.nickname,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${"%.1f".format(user.distance)}km",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(end = 16.dp)
        )
        Text(
            text = "\uD83D\uDCA1 x ${user.itemCount}",
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

// 여러 사용자 정보를 표시하는 메인 화면 컴포저블
@Composable
fun MultiUserScreen(viewModel: MultiUserViewModel) {
    val userList by viewModel.userList.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(userList) { user ->
                UserInfoCard(user)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
