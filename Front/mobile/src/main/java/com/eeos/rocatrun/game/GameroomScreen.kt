package com.eeos.rocatrun.game

//import com.eeos.rocatrun.socket.SocketHandler
import android.content.Intent
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eeos.rocatrun.R
import com.eeos.rocatrun.home.HomeActivity
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
    var selectedDifficulty by remember { mutableStateOf("") }
    var selectedPeople by remember { mutableStateOf("") }
    var code by remember { mutableStateOf<String?>(null) }

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
                    generatedCode = code,

                    //생성 버튼 누를때 request 보내줘야됨.
                    onGenerateClick = {

                        // 난이도 변환 : "상" -> "HARD", "중" -> "MEDIUM", "하" -> "EASY"
                        val bossLevel = when (selectedDifficulty) {
                            "상" -> "HARD"
                            "중" -> "MEDIUM"
                            "하" -> "EASY"
                            else -> "EASY" // 기본값 설정 - 2가지 선택 안되면 생성 버튼 비활성화 시켜야 될 듯
                        }

                        // 인원 수 변환: "1인" -> 1, "2인" -> 2, …
                        val maxPlayers = when (selectedPeople) {
                            "1인" -> 1
                            "2인" -> 2
                            "3인" -> 3
                            "4인" -> 4
                            else -> 2 // 기본값 설정
                        }

                        // 웹소켓 요청 보냄
                        CreateRoomSocket(bossLevel, maxPlayers)

                        code = "TK3NBF" // 임시로 6자 하드코딩
                    }
                )
            }
        }
    }
}

// 코드 생성
@Composable
fun CodeGenerationSection(
    generatedCode: String? = null,
    onGenerateClick: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 코드 생성 버튼
        Box(
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = Color(0xFFFF00CC),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { onGenerateClick() }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                "코드 생성",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp
                )
            )
        }

        // 코드 표시 영역과 복사 버튼
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.width(160.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(30.dp)
            ) {
                if (generatedCode != null) {
                    Text(
                        text = generatedCode,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 25.sp
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(Color.White)
                            .align(Alignment.BottomCenter)
                    )
                }
            }
            // 복사 버튼
            IconButton(
                onClick = {
                    generatedCode?.let {
                        clipboardManager.setText(AnnotatedString(it))
                        Toast.makeText(
                            context,
                            "코드가 복사되었습니다",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.size(24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.game_icon_copy),
                    contentDescription = "Copy code",
                    modifier = Modifier.size(24.dp)
                )
            }
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
                            // Loading으로 이동. 근데 여기서 코드 있는건지 없는건지 확인해서 예외처리 작업 필요함
                            val intent = Intent(context, Loading::class.java)
                            context.startActivity(intent)
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
}

// 랜덤 찾기 extend 창
@Composable
fun RandomContent(onBack: () -> Unit) {
    val context = LocalContext.current
    var randomDifficulty by remember { mutableStateOf("") }
    var randomPeople by remember { mutableStateOf("") }

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
                    .border(
                        width = 2.dp,
                        color = Color(0xFF6A00F4),
                        shape = RoundedCornerShape(10.dp)
                    )
                    // 입장 클릭하면 대기중 화면 띄우기
                    .clickable {
                        // Matching으로 이동
                        val intent = Intent(context, Matching::class.java)
                        context.startActivity(intent)
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

            Spacer(modifier = Modifier.height(15.dp))
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

// 방 생성 소켓 연결
fun CreateRoomSocket(bossLevel: String, maxPlayers: Int) {
    // 화면이 생성될 때 소켓 연결 및 리스너 등록
    // 컴포저블이 사라질 때(더 이상 정보 필요하지 않을 때 리스너 해제)

    // 전송할 JSON 생성
    val createRoomJson = JSONObject().apply {
        put("bossLevel", bossLevel)    // or "MEDIUM", "HARD"
        put("maxPlayers", maxPlayers)        // 1-4 사이의 숫자
//        put("isPrivate", true)      // 비밀 방 여부(초대코드 생성 여부)
    }

    Log.d("Socket", "emit")

    // 메세지 전송
//    SocketHandler.socket.emit("createRoom", createRoomJson)

}

