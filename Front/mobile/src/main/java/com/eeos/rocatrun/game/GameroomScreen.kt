package com.eeos.rocatrun.game

//import com.eeos.rocatrun.socket.SocketHandler
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eeos.rocatrun.R
import com.eeos.rocatrun.home.HomeActivity
import com.eeos.rocatrun.socket.SocketHandler
import org.json.JSONObject


@Composable
fun GameroomScreen() {
    var showInfoDialog by remember {
        mutableStateOf(false)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.game_bg_gameroom),
            contentDescription = "game room select background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp)
        ) {
            // Top Navigation Icons
            TopNavigation(
                onInfoClick = {
                    showInfoDialog = true
                }
            )

            // Main Buttons
            MainButtons()
        }

        if (showInfoDialog) {
            InfoScreen(
                onDismissRequest = {
                    showInfoDialog = false
                }
            )
        }
    }
}

// 상단 홈, 정보 아이콘 버튼
@Composable
fun TopNavigation(
    onInfoClick: () -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp) // 상태바로부터의 거리 조정
            .padding(horizontal = 16.dp) // 좌우 여백 추가
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = {
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
        }) {
            Image(
                painter = painterResource(id = R.drawable.game_icon_home),
                contentDescription = "Home",
                modifier = Modifier.size(180.dp),
            )
        }

        IconButton(onClick = onInfoClick ) {
            Image(
                painter = painterResource(id = R.drawable.game_icon_inform),
                contentDescription = "Information",
                modifier = Modifier.size(160.dp),
            )
        }
    }
}

// 메인 버튼 3개
@Composable
fun MainButtons() {
    val context = LocalContext.current
    var selectedButton by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        // 방 만들기 버튼
        if (selectedButton == "create") {
            CreateRoomContent(
                onBack = { selectedButton = null }
            )
        } else {
            CustomButton(
                buttonImage = R.drawable.game_btn_darkpink,
                buttonText = "방 만들기",
                iconImage = R.drawable.game_icon_meetingroom,
                onClick = { selectedButton = "create" }
            )
        }

        // 초대코드 입력 버튼
        if (selectedButton == "invite") {
            InviteCodeContent(
                onBack = { selectedButton = null }
            )
        } else {
            CustomButton(
                buttonImage = R.drawable.game_btn_darkpurple,
                buttonText = "초대코드 입력",
                iconImage = R.drawable.game_icon_password,
                onClick = { selectedButton = "invite" }
            )
        }

        // 랜덤 찾기 버튼
        if (selectedButton == "random") {
            RandomContent(
                onBack = { selectedButton = null }
            )
        } else {
            CustomButton(
                buttonImage = R.drawable.game_btn_darkblue,
                buttonText = "랜덤 찾기",
                iconImage = R.drawable.game_icon_random,
                // 클릭하면 랜덤 찾는중 화면 이동
                onClick = { selectedButton = "random" }
            )
        }
    }
}

// 방 생성 extend 창
@Composable
fun CreateRoomContent(onBack: () -> Unit) {
    val context = LocalContext.current
    var selectedDifficulty by remember { mutableStateOf("") }
    var selectedPeople by remember { mutableStateOf("") }
    var code by remember { mutableStateOf<String?>(null) }

    // 두 항목 모두 선택되어야 생성 버튼이 활성화됨
    val isGenerateEnabled = selectedDifficulty.isNotBlank() && selectedPeople.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 3.dp,
                color = Color(0xFFFF00CC)
            )
            .background(color = Color(0xB2000000))
            .padding(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFF00CC))
                    .height(50.dp)
            ){
                Text(
                    text = "방 만들기",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.Black,
                        fontSize = 30.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }

            // 난이도 선택
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 25.dp)
                ) {
                    Text(
                        text = ">> ",
                        color = Color(0xFFFF00CC),
                        style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "난이도를 선택해 주세요",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SelectableText(
                        text = "상",
                        isSelected = selectedDifficulty == "상",
                        onClick = { selectedDifficulty = "상" }
                    )
                    SelectableText(
                        text = "중",
                        isSelected = selectedDifficulty == "중",
                        onClick = { selectedDifficulty = "중" }
                    )
                    SelectableText(
                        text = "하",
                        isSelected = selectedDifficulty == "하",
                        onClick = { selectedDifficulty = "하" }
                    )
                }
            }

            // 인원 선택
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 25.dp)
                ) {
                    Text(
                        text = ">> ",
                        color = Color(0xFFFF00CC),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "인원을 선택해 주세요",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SelectableText(
                        text = "1인",
                        isSelected = selectedPeople == "1인",
                        onClick = { selectedPeople = "1인" }
                    )
                    SelectableText(
                        text = "2인",
                        isSelected = selectedPeople == "2인",
                        onClick = { selectedPeople = "2인" }
                    )
                    SelectableText(
                        text = "3인",
                        isSelected = selectedPeople == "3인",
                        onClick = { selectedPeople = "3인" }
                    )
                    SelectableText(
                        text = "4인",
                        isSelected = selectedPeople == "4인",
                        onClick = { selectedPeople = "4인" }
                    )
                }
            }

            // 코드 생성 및 소켓 요청 전송
            Box(
                modifier = Modifier.padding(bottom = 20.dp)
            ){
                CodeGenerationSection(
                    enabled = isGenerateEnabled,

                    onGenerateClick = {

                        // 난이도 변환 : "상" -> "HARD", "중" -> "MEDIUM", "하" -> "EASY"
                        val bossLevel = when (selectedDifficulty) {
                            "상" -> "HARD"
                            "중" -> "MEDIUM"
                            "하" -> "EASY"
                            else -> "EASY" // 기본값 설정 - 2가지 선택 안되면 생성 버튼 비활성화 시켜야 될 듯
                        }

                        // 인원 수 변환: "1인" -> 1, "2인" -> 2, …
                        val roomPlayers = when (selectedPeople) {
                            "1인" -> 1
                            "2인" -> 2
                            "3인" -> 3
                            "4인" -> 4
                            else -> 2 // 기본값 설정
                        }

                        // 방생성 - 웹소켓
                        CreateRoomSocket(bossLevel, roomPlayers) { inviteCode, currentPlayers, maxPlayers ->

                            val intent = Intent(context, Loading::class.java)
                            intent.putExtra("inviteCode", inviteCode)
                            intent.putExtra("currentPlayers", currentPlayers)
                            intent.putExtra("maxPlayers", maxPlayers)
                            context.startActivity(intent)
                        }
                    }
                )
            }
        }
    }
}

// 방 생성 버튼
@Composable
fun CodeGenerationSection(
    enabled: Boolean,
    onGenerateClick: () -> Unit
) {
    val borderColor = if (enabled) Color(0xFFFF00CC) else Color.Gray // 활성/비활성 보더 색상

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 방 생성 버튼
        Box(
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(10.dp)
                )
                // 버튼이 활성화되었을 때만 클릭 이벤트 반응
                .then(if (enabled) Modifier
                    .clickable {
                        onGenerateClick()
                    } else Modifier)

                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                "방 생성",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
            )
        }
    }
}

// 초대코드 입력 extend 창
@Composable
fun InviteCodeContent(onBack: () -> Unit) {
    val context = LocalContext.current
    var inviteCode by remember { mutableStateOf("") }
    // 코드 최대 글자
    val maxLength = 6

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 3.dp,
                color = Color(0xFFCC00FF)
            )
            .background(color = Color(0xB2000000))
            .padding(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            // 헤더
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFCC00FF))
                    .height(50.dp)
            ) {
                Text(
                    text = "초대코드 입력",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.Black,
                        fontSize = 30.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }

            // 코드 입력 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(maxLength) { index ->
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(40.dp)
                        ) {
                            if (index < inviteCode.length) {
                                Text(
                                    text = inviteCode[index].toString(),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 25.sp
                                    ),
                                    color = Color.White,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(Color.White)
                            )
                        }
                    }
                }

                BasicTextField(
                    value = inviteCode,
                    onValueChange = { newValue ->
                        if (newValue.length <= maxLength) {
                            inviteCode = newValue.uppercase()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(0.dp), // TextField의 크기를 0으로 설정
                    textStyle = TextStyle(color = Color.White),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(30.dp))

                // 입장 버튼
                Box(
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            color = Color(0xFFCC00FF),
                            shape = RoundedCornerShape(10.dp)
                        )
                        // 입장 클릭하면 대기중 화면 띄우기
                        .clickable {

                            // 웹소켓 입장 이벤트 호출: 성공하면 LoadingActivity로 이동, 에러면 모달 띄움
                            JoinRoomSocket(inviteCode = inviteCode,
                                onSuccess = { rInviteCode, currentPlayers, maxPlayers ->

                                    // 성공하면 LoadingActivity로 이동
                                    val intent = Intent(context, Loading::class.java).apply {
                                        putExtra("inviteCode", rInviteCode)
                                        putExtra("currentPlayers", currentPlayers)
                                        putExtra("maxPlayers", maxPlayers)
                                    }

                                    context.startActivity(intent)
                                },
                                onError = { error ->

                                    // 실패하면 모달 띄우기
                                    errorMessage = error
                                    showErrorDialog = true
                                }
                            )
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "입장",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // 에러가 발생했을 때 모달 다이얼로그 표시
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("에러 발생") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("확인")
                }
            }
        )
    }
}

// 랜덤 찾기 extend 창
@Composable
fun RandomContent(onBack: () -> Unit) {
    val context = LocalContext.current
    var randomDifficulty by remember { mutableStateOf("") }
    var randomPeople by remember { mutableStateOf("") }

    // 두 항목 모두 선택되어야 생성 버튼이 활성화됨
    val isRandomMatchEnabled = randomDifficulty.isNotBlank() && randomPeople.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 3.dp,
                color = Color(0xFF6A00F4)
            )
            .background(color = Color(0xB2000000))
            .padding(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF6A00F4))
                    .height(50.dp)
            ){
                Text(
                    text = "랜덤 찾기",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.Black,
                        fontSize = 30.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }

            // 난이도 선택
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 25.dp)
                ) {
                    Text(
                        text = ">> ",
                        color = Color(0xFF6A00F4),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "난이도를 선택해 주세요",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RandomText(
                        text = "상",
                        isSelected = randomDifficulty == "상",
                        onClick = { randomDifficulty = "상" }
                    )
                    RandomText(
                        text = "중",
                        isSelected = randomDifficulty == "중",
                        onClick = { randomDifficulty = "중" }
                    )
                    RandomText(
                        text = "하",
                        isSelected = randomDifficulty == "하",
                        onClick = { randomDifficulty = "하" }
                    )
                }
            }

            // 인원 선택
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 25.dp)
                ) {
                    Text(
                        text = ">> ",
                        color = Color(0xFF6A00F4),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "인원을 선택해 주세요",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RandomText(
                        text = "1인",
                        isSelected = randomPeople == "1인",
                        onClick = { randomPeople = "1인" }
                    )
                    RandomText(
                        text = "2인",
                        isSelected = randomPeople == "2인",
                        onClick = { randomPeople = "2인" }
                    )
                    RandomText(
                        text = "3인",
                        isSelected = randomPeople == "3인",
                        onClick = { randomPeople = "3인" }
                    )
                    RandomText(
                        text = "4인",
                        isSelected = randomPeople == "4인",
                        onClick = { randomPeople = "4인" }
                    )
                }
            }
            // 입장 버튼
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp)
            ){
                RandomMatchSection(
                    enabled = isRandomMatchEnabled,
                    onRandomMatchClick = {

                        // 난이도 변환 : "상" -> "HARD", "중" -> "MEDIUM", "하" -> "EASY"
                        val bossLevel = when (randomDifficulty) {
                            "상" -> "HARD"
                            "중" -> "MEDIUM"
                            "하" -> "EASY"
                            else -> "EASY" // 기본값 설정 - 2가지 선택 안되면 생성 버튼 비활성화 시켜야 될 듯
                        }

                        // 인원 수 변환: "1인" -> 1, "2인" -> 2, …
                        val roomPlayers = when (randomPeople) {
                            "1인" -> 1
                            "2인" -> 2
                            "3인" -> 3
                            "4인" -> 4
                            else -> 2 // 기본값 설정
                        }

                        RandomMatchSocket(bossLevel, roomPlayers) { currentPlayers, maxPlayers ->
                            // Matching으로 이동
                            val intent = Intent(context, Matching::class.java)
                            intent.putExtra("currentPlayers", currentPlayers)
                            intent.putExtra("maxPlayers", maxPlayers)
                            context.startActivity(intent)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

// 랜덤 찾기 버튼
@Composable
fun RandomMatchSection(
    enabled: Boolean,
    onRandomMatchClick: () -> Unit
) {
    val borderColor = if (enabled) Color(0xFF6A00F4) else Color.Gray // 활성/비활성 보더 색상

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 방 생성 버튼
        Box(
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(10.dp)
                )
                // 버튼이 활성화되었을 때만 클릭 이벤트 반응
                .then(if (enabled) Modifier
                    .clickable {
                        onRandomMatchClick()
                    } else Modifier)

                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                "방 생성",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
            )
        }
    }
}

// 메인 3개 버튼 양식
@Composable
private fun CustomButton(
    buttonImage: Int,
    buttonText: String,
    iconImage: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = buttonImage),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 25.sp,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(id = iconImage),
                contentDescription = null,
                modifier = Modifier.size(42.dp)
            )
        }
    }
}

// 방 선택한 텍스트 색 변경
@Composable
private fun SelectableText(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        // Stroke 텍스트
        Text(
            text = text,
            color = if (isSelected) Color(0xFFFF00CC) else Color(0xFF796D76),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 25.sp,
                drawStyle = Stroke(
                    width = 10f,
                    join = StrokeJoin.Round
                )
            )
        )
        // 기본 텍스트
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 25.sp
            )
        )
    }
}

// 랜덤 선택한 텍스트 색 변경
@Composable
private fun RandomText(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        // Stroke 텍스트
        Text(
            text = text,
            color = if (isSelected) Color(0xFF6A00F4) else Color(0xFF796D76),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 25.sp,
                drawStyle = Stroke(
                    width = 10f,
                    join = StrokeJoin.Round
                )
            )
        )
        // 기본 텍스트
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 25.sp
            )
        )
    }
}

// 웹소켓 방 생성 이벤트
fun CreateRoomSocket(
    bossLevel: String,
    roomPlayers: Int,
    onRoomCreated: (inviteCode: String,
                    currentPlayers: Int,
                    maxPlayers: Int) -> Unit)
{
    // 전송할 JSON 생성
    val createRoomJson = JSONObject().apply {
        put("bossLevel", bossLevel)    // or "MEDIUM", "HARD"
        put("maxPlayers", roomPlayers)        // 1-4 사이의 숫자
        put("isPrivate", true)      // 비밀 방 여부(초대코드 생성 여부)
    }
    Log.d("Socket", "Emit - createRoom")

    // 방 생성 전송
    SocketHandler.mSocket.emit("createRoom", createRoomJson)

    // 방 생성 응답 이벤트 리스너 등록
    SocketHandler.mSocket.on("roomCreated") { args ->
        if (args.isNotEmpty() && args[0] is JSONObject) {
            val json = args[0] as JSONObject
            val roomId = json.optString("roomId", "")
            val inviteCode = json.optString("inviteCode", "")
            val currentPlayers = json.optInt("currentPlayers", 0)
            val maxPlayers = json.optInt("maxPlayers", 0)

            // JSON 데이터 로그 출력
            Log.d(
                "Socket",
                "On - roomCreated: roomId: $roomId, inviteCode: $inviteCode, currentPlayers: $currentPlayers, maxPlayers: $maxPlayers"
            )

            // 콜백으로 초대코드, 현재수, 정원수 전달
            onRoomCreated(inviteCode, currentPlayers, maxPlayers)
        }
    }
}

// 웹소켓 입장 이벤트
fun JoinRoomSocket(
    inviteCode: String,
    onSuccess: (inviteCode: String,
                   currentPlayers: Int,
                   maxPlayers: Int) -> Unit,
    onError: (String) -> Unit)
{
    // 전송할 JSON 생성
    val joinRoomJson = JSONObject().apply {
        put("inviteCode", inviteCode)

    }
    Log.d("Socket", "Emit - joinRoom")

    // 입장 요청
    SocketHandler.mSocket.emit("joinRoom", joinRoomJson)

    // 입장 응답 이벤트 리스너 등록
    SocketHandler.mSocket.off("roomJoined") // 중복 등록 방지
    SocketHandler.mSocket.on("roomJoined") { args ->
        if (args.isNotEmpty() && args[0] is JSONObject) {

            val json = args[0] as JSONObject

            // 만약 json에 "error" 키가 있으면 에러 처리
            if (json.has("error")) {
                val errorMsg = json.optString("error", "알 수 없는 에러")
                Log.d("Socket", "Error - roomJoined : $errorMsg")
                onError(errorMsg)
            } else
            {
                // 정상 응답일 경우
                val roomId = json.optString("roomId", "")
                val extractedInviteCode = json.optString("inviteCode", "")
                val currentPlayers = json.optInt("currentPlayers", 0)
                val maxPlayers = json.optInt("maxPlayers", 0)

                Log.d(
                    "Socket",
                    "On - roomJoined : userId: $roomId, inviteCode: $extractedInviteCode, currentPlayers: $currentPlayers, maxPlayers: $maxPlayers"
                )
                // 콜백으로 초대코드, 현재수, 정원수 전달
                onSuccess(inviteCode, currentPlayers, maxPlayers)
            }
        } else {
           Log.d("Socket", "Error - roomJoined : 잘못된 응답")
        }
    }
}

// 랜덤매칭 이벤트
fun RandomMatchSocket(
    bossLevel: String,
    roomPlayers: Int,
    matchCreated: (currentPlayers: Int,
                    maxPlayers: Int) -> Unit)
{
    // 전송할 JSON 생성
    val randomMatchJson = JSONObject().apply {
        put("bossLevel", bossLevel)    // or "MEDIUM", "HARD"
        put("maxPlayers", roomPlayers)        // 1-4 사이의 숫자
    }
    Log.d("Socket", "Emit - randomMatch")

    // 방 생성 전송
    SocketHandler.mSocket.emit("randomMatch", randomMatchJson)

    // 방 생성 응답 이벤트 리스너 등록
    SocketHandler.mSocket.on("matchStatus") { args ->
        if (args.isNotEmpty() && args[0] is JSONObject) {
            val json = args[0] as JSONObject
            val roomId = json.optString("userId", "")
            val currentPlayers = json.optInt("currentPlayers", 0)
            val maxPlayers = json.optInt("maxPlayers", 0)

            // JSON 데이터 로그 출력
            Log.d(
                "Socket",
                "On - matchStatus: roomId: $roomId, currentPlayers: $currentPlayers, maxPlayers: $maxPlayers"
            )

            // 콜백으로 초대코드, 현재수, 정원수 전달
            matchCreated(currentPlayers, maxPlayers)
        }
    }
}