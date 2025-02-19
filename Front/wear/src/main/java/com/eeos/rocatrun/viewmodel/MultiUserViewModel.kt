package com.eeos.rocatrun.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import androidx.lifecycle.AndroidViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import com.eeos.rocatrun.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import com.eeos.rocatrun.ui.CircularItemGauge
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import java.util.*
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.zIndex

// ViewModel 정의
class MultiUserViewModel(application: Application) : AndroidViewModel(application), DataClient.OnDataChangedListener {

    private var tts : TextToSpeech? = null
    private lateinit var dataClient: DataClient
    // 플레이어 리스트
    private val _playerList = MutableStateFlow<List<PlayerData>>(emptyList())
    val playerList: StateFlow<List<PlayerData>> get() = _playerList
    // 실시간 플레이어 데이터(닉네임을 key로 관리)
    private val _playersDataMap = MutableStateFlow<Map<String, PlayersData>>(emptyMap())
    val playersDataMap: StateFlow<Map<String, PlayersData>> get() = _playersDataMap

    // 종료된 플레이어
    private val _playerLeftMessage = MutableStateFlow<String?>(null)
    val playerLeftMessage: StateFlow<String?> get() = _playerLeftMessage

    private var lastPlayerDataReceivedTime = System.currentTimeMillis()
    private var firstPlayerDataReceivedTime = System.currentTimeMillis()

    // 네트워크 에러 이벤트 플로우
    private val _networkErrorEventFlow = MutableSharedFlow<Boolean>()
    val networkErrorEventFlow: SharedFlow<Boolean> get() = _networkErrorEventFlow

    private var playersData by mutableStateOf<PlayersData?>(null)
    private var bossHealthData by mutableStateOf<BossHealthData?>(null)
    private var feverEndData by mutableStateOf<FeverEndData?>(null)
    private var feverStartData by mutableStateOf<FeverStartData?>(null)
    private var firstBossHealthData by mutableStateOf<FirstBossHealthData?>(null)
    private var gameEndData by mutableStateOf<GameEndData?>(null)

    private var firstDataReceived = false

    private var startDataReceived = false


    // 데이터 클래스 정의

    data class PlayerData(
        val nickname: String
    )

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
    // 난이도 별 보스 데이터
    data class FirstBossHealthData(
        val firstBossHealth: Int
    )
    // 게임 종료 데이터
    data class GameEndData(
        val gameEnd: Boolean
    )



    // 피버 이벤트 플로우
    private val _feverEventFlow = MutableSharedFlow<Boolean>()
    val feverEventFlow: SharedFlow<Boolean> get() = _feverEventFlow


    // 게임 종료 이벤트 플로우
    private val _gameEndEventFlow = MutableSharedFlow<Boolean>()
    val gameEndEventFlow: SharedFlow<Boolean> get() = _gameEndEventFlow

    private val context = application.applicationContext


    // 테스트용 데이터 주기적 업데이트
    init {
        tts = TextToSpeech(getApplication()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.setLanguage(Locale.KOREAN)
            } else {
                Log.e("MultiUserViewModel", "TTS 초기화 실패")
            }
        }


        viewModelScope.launch {
            dataClient = Wearable.getDataClient(context)
            dataClient.addListener(this@MultiUserViewModel)
            Log.d("MultiUserViewModel", "DataClient listener 등록됨")

            // 추가: 리스너 등록 후, 이미 캐시되어 있는 /first_boss_health 데이터 조회
            dataClient.getDataItems().addOnSuccessListener { dataItemBuffer ->
                for (dataItem in dataItemBuffer) {
                    if (dataItem.uri.path == "/first_boss_health") {
                        processFirstBossHealthData(dataItem)
                    }
                }
                dataItemBuffer.release()
            }.addOnFailureListener { exception ->
                Log.e("MultiUserViewModel", "캐시된 데이터 조회 실패", exception)
            }
        }

    }

    // ViewModel이 소멸될 때 호출되는 메서드
    override fun onCleared() {
        super.onCleared()
        dataClient.removeListener(this)
        tts?.shutdown()
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
                    "/first_boss_health" -> processFirstBossHealthData(dataItem)
                    "/game_end" -> processGameEndData(dataItem)
                    "/player_left" -> processPlayerLeftData(dataItem)
                }
            }

        }
    }
    // 실시간 유저 데이터
    // /players_data 이벤트: 실시간 데이터 업데이트 → Map에 저장
    private fun processPlayersData(dataItem: DataItem) {
        val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
        val nickname = dataMap.getString("nickName") ?: "Unknown"
        val distance = dataMap.getDouble("distance")
        val itemCount = dataMap.getInt("itemUseCount")
        val newData = PlayersData(nickname, distance, itemCount)
        _playersDataMap.value = _playersDataMap.value.toMutableMap().apply {
            put(nickname, newData)
        }
        lastPlayerDataReceivedTime = System.currentTimeMillis()
        Log.d("MultiUserViewModel", "실시간 플레이어 데이터 업데이트: $newData")
        if (!firstDataReceived) {
            firstDataReceived = true
            viewModelScope.launch {
                while (true) {
                    delay(2000) // 1초마다 체크
                    if (System.currentTimeMillis() - lastPlayerDataReceivedTime > 3000) {
                        _networkErrorEventFlow.emit(true)
                        Log.d("MultiUserViewModel", "네트워크 연결 끊김 감지, 네트워크 에러 이벤트 발행")
                        break
                    }
                }
            }
        }
    }

    // 실시간 보스 체력 데이터 (Repository에 업데이트)
    private fun processBossHealthData(dataItem: DataItem){
        DataMapItem.fromDataItem(dataItem).dataMap.apply{
            val health = getInt("bossHealth")
            bossHealthData = BossHealthData(bossHealth = health)
            BossHealthRepository.updateBossHealth(health)
        }
        Log.d("MultiUserViewModel", "보스 체력 데이터 받는중 : $bossHealthData" )
    }
    // 피버 시작 데이터
    private fun processFeverStartData(dataItem: DataItem){
        DataMapItem.fromDataItem(dataItem).dataMap.apply{
            feverStartData = FeverStartData(
                feverstart = getBoolean("feverStart")
            )
        }
        Log.d("MultiUserViewModel", "피버 시작 데이터 받는중 : $feverStartData")
        viewModelScope.launch { _feverEventFlow.emit(true) }  // 시작 이벤트 전송
    }
    // 피버 종료 데이터
    private fun processFeverEndData(dataItem: DataItem){
        DataMapItem.fromDataItem(dataItem).dataMap.apply{
            feverEndData = FeverEndData(
                feverend = getBoolean("feverEnd")
            )
        }
        Log.d("MultiUserViewModel", "피버 종료 데이터 받는중 : $feverEndData")
        viewModelScope.launch { _feverEventFlow.emit(false) }  // 종료 이벤트 전송
    }

    // 난이도 별 보스 데이터(초기 보스 체력 데이터를 받아서 Repository에 반영)
    private fun processFirstBossHealthData(dataItem: DataItem){
        DataMapItem.fromDataItem(dataItem).dataMap.apply {
            val health = getInt("firstBossHealth")
            firstBossHealthData = FirstBossHealthData(firstBossHealth = health)
            // 이 값을 최대 체력으로 사용하고, 동시에 현재 체력으로도 반영
            bossHealthData = BossHealthData(bossHealth = health)
            BossHealthRepository.updateMaxBossHealth(health)
            BossHealthRepository.updateBossHealth(health)
            val nicknames = getStringArrayList("playerNicknames")
            if (nicknames != null) {
                val players = nicknames.map { PlayerData(it) }
                _playerList.value = players
                Log.d("MultiUserViewModel", "플레이어 목록 업데이트: $players")
            } else {
                Log.w("MultiUserViewModel", "playerNicknames 데이터가 null 입니다.")
            }
            val time = getInt("time")
            BossHealthRepository.setGameTime(time)
            lastPlayerDataReceivedTime = System.currentTimeMillis()
            if (!startDataReceived) {
                startDataReceived = true
                viewModelScope.launch {
                    while (true) {
                        delay(1500)
                        if (System.currentTimeMillis() - lastPlayerDataReceivedTime > 7000) {
                            _networkErrorEventFlow.emit(true)
                            Log.d("MultiUserViewModel", "네트워크 연결 끊김 감지, 네트워크 에러 이벤트 발행")
                            break
                        }
                    }
                }
            }
        }
        Log.d("MultiUserViewModel", "보스 초기 체력 데이터 : $firstBossHealthData")
    }
    // 게임 종료 데이터
    private fun processGameEndData(datItem: DataItem){
        DataMapItem.fromDataItem(datItem).dataMap.apply {
            gameEndData = GameEndData(
                gameEnd = getBoolean("gameEnd")
            )
            viewModelScope.launch {
                delay(500)
                BossHealthRepository.updateBossHealth(0)
                _gameEndEventFlow.emit(true)
                Log.d("게임 종료", "게임 종료 이벤트 플로우 : $gameEndEventFlow")

            }
        }
        Log.d("MultiUserViewModel", "게임 종료 데이터 받는중 : $gameEndData")
    }

    private fun processPlayerLeftData(dataItem: DataItem){
        val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
        val nickName  = dataMap.getString("nickName")
        if(nickName != null){
            _playersDataMap.value = _playersDataMap.value.toMutableMap().apply {
                remove(nickName)
            }
            _playerList.value = _playerList.value.filter { it.nickname != nickName }
            tts?.speak("$nickName 님이 떠났습니다.", TextToSpeech.QUEUE_FLUSH, null, null)
            Log.d("MultiUserViewModel", "플레이어 퇴장: $nickName")

        }else{
            Log.d("MultiUserViewModel" , " playerLeft 데이터에 nickName이 없음")
        }
    }

}

// 사용자 정보를 표시하는 카드 컴포저블
@Composable
fun UserInfoCard(player: MultiUserViewModel.PlayerData, realTimeData: MultiUserViewModel.PlayersData) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .height(60.dp)
            .background(Color(0xFF1C1C1C), shape = RoundedCornerShape(16.dp))
            .padding(8.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = player.nickname,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.neodgm)),
            )
            Text(
                text = " ${"%.2f".format(realTimeData.distance)}km",
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.neodgm)),
            )
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = R.drawable.wear_icon_fish),
                contentDescription = "Item Icon",
                modifier = Modifier
                    .size(35.dp)
            )
            Text(
                text = "x ${realTimeData.itemCount}",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.neodgm)),
                fontSize = 16.sp
            )

        }

    }
}

// 여러 사용자 정보를 표시하는 메인 화면 컴포저블
@Composable
fun MultiUserScreen(viewModel: MultiUserViewModel, gameViewModel: GameViewModel) {
    // 플레이어들 데이터
    val playerList by viewModel.playerList.collectAsState()
    val playersDataMap by viewModel.playersDataMap.collectAsState()

    // GameViewModel에서 가져온 게이지 값
    val itemGaugeValue by gameViewModel.itemGaugeValue.collectAsState()
    val bossGaugeValue by gameViewModel.bossGaugeValue.collectAsState()
    val maxGaugeValue = 100

    // BossHealthRepository의 최대 체력 구독 (최초 값이 0이라면 기본값 10000 사용)
    val maxBossHealth by BossHealthRepository.maxBossHealth.collectAsState()
    val effectiveMaxBossHealth = if (maxBossHealth == 0) 10000 else maxBossHealth
    val isFeverTime by gameViewModel.feverTimeActive.collectAsState()

    val itemProgress by animateFloatAsState(
        targetValue = itemGaugeValue.toFloat() / maxGaugeValue,
        animationSpec = tween(durationMillis = 500)
    )
    val bossProgress by animateFloatAsState(
        targetValue = bossGaugeValue.toFloat() / effectiveMaxBossHealth,
        animationSpec = tween(durationMillis = 500)
    )

    // 표시할 플레이어 리스트를 미리 준비
    val displayPlayers = playerList.take(4)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 스크롤로 최대 4개 보이게 함
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .height(180.dp)  // 스크롤 영역 높이 제한
                .align(Alignment.TopCenter)
                .offset(y = 15.dp)
                .zIndex(0f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)  // 카드 간격
        )  {
            items(displayPlayers.size) { index ->
                val player = displayPlayers[index]  // index로 실제 player 객체 가져오기
                val realTimeData = playersDataMap[player.nickname]
                if (realTimeData != null) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        UserInfoCard(player, realTimeData)
                    }
                }
            }
        }
        // 중앙에 원형 게이지 표시 - 제일 위로 보이게 설정
        CircularItemGauge(
            itemProgress = itemProgress,
            bossProgress = bossProgress,
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
                .zIndex(1f),
            isFeverTime

        )
    }
}
