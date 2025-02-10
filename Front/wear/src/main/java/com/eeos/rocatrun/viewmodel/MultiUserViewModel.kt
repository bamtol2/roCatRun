package com.eeos.rocatrun.viewmodel

import android.app.Application
import android.content.Context
import android.provider.ContactsContract.Data
import android.util.Log
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
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import androidx.lifecycle.AndroidViewModel


data class UserData(
    val nickname: String,
    val distance: Double,
    val itemCount: Int
)
// ViewModel 정의
class MultiUserViewModel(application: Application) : AndroidViewModel(application), DataClient.OnDataChangedListener {

    private lateinit var dataClient: DataClient
    private val _userList = MutableStateFlow<List<UserData>>(emptyList())

    private var playersData by mutableStateOf<PlayersData?>(null)
    private var bossHealthData by mutableStateOf<BossHealthData?>(null)
    private var feverEndData by mutableStateOf<FeverEndData?>(null)
    private var feverStartData by mutableStateOf<FeverStartData?>(null)
    // 실시간으로 받아올 사용자들의 러닝 데이터


    // 데이터 클래스 정의

    // 실시간 유저 러닝 데이터
    data class PlayersData(
        val nickname: String,
        val distance: Double,
        val itemCount: Int
    )

    // 피버 시작 데이터
    data class FeverStartData(
        val feverstart : Boolean
    )
    // 피버 종료 데이터
    data class FeverEndData(
        val feverend : Boolean
    )
    // 보스 체력 데이터
    data class BossHealthData(
        val bossHealth: Int
    )


    private val context = application.applicationContext

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

    // ViewModel이 소멸될 때 호출되는 메서드
    override fun onCleared() {
        super.onCleared()
        dataClient.removeListener(this)
        Log.d("MultiUserViewModel", "데이터 리스너 제거됨")
    }

    override fun onDataChanged(dataEvents: DataEventBuffer){
        dataEvents.forEach{ event ->
            if(event.type == DataEvent.TYPE_CHANGED){
                val dataItem = event.dataItem
                when(dataItem.uri.path){
                    "/players_data" -> processPlayersData(dataItem)
                    "/boss_health" -> processBossHealthData(dataItem)
                    "/fever_start" -> processFeverStartData(dataItem)
                    "/fever_end" -> processFeverEndData(dataItem)

                }
            }

        }
    }
    // 실시간 유저 데이터
    private fun processPlayersData(dataItem: DataItem) {
        DataMapItem.fromDataItem(dataItem).dataMap.apply {
            playersData = PlayersData(
                nickname = getString("nickname") ?: "Unknown",
                distance = getDouble("distance"),
                itemCount = getInt("itemUsed")
            )
        }
        Log.d("Multi", "사용자 데이터 받는중 : $playersData")
    }

    // 실시간 보스 체력 데이터
    private fun processBossHealthData(dataItem: DataItem){
        DataMapItem.fromDataItem(dataItem).dataMap.apply{
            bossHealthData = BossHealthData(
                bossHealth = getInt("bosshealth")
            )
        }
        Log.d("Multi", "보스 체력 데이터 받는중 : $bossHealthData" )
    }

    private fun processFeverStartData(dataItem: DataItem){
        DataMapItem.fromDataItem(dataItem).dataMap.apply{
            feverStartData = FeverStartData(
                feverstart = getBoolean("feverStart")
            )
        }
        Log.d("Multi", "피버 시작 데이터 받는중 : $feverStartData")
    }

    private fun processFeverEndData(dataItem: DataItem){
        DataMapItem.fromDataItem(dataItem).dataMap.apply{
            feverEndData = FeverEndData(
                feverend = getBoolean("feverEnd")
            )
        }
        Log.d("Multi", "피버 종료 데이터 받는중 : $feverEndData")
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
