package com.eeos.rocatrun.game

import android.content.Intent
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
import androidx.compose.runtime.rememberCoroutineScope
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


@Composable
fun GameroomScreen() {
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
            TopNavigation()

            // Main Buttons
            MainButtons()
        }
    }
}

// 상단 홈, 정보 아이콘 버튼
@Composable
fun TopNavigation() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp) // 상태바로부터의 거리 조정
            .padding(horizontal = 16.dp) // 좌우 여백 추가
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { /* 홈 으로 이동 */ }) {
            Image(
                painter = painterResource(id = R.drawable.game_icon_home),
                contentDescription = "Home",
                modifier = Modifier.size(180.dp),
            )
        }

        IconButton(onClick = { /* 보스 정보 모달 띄우기 */ }) {
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
        CustomButton(
            buttonImage = R.drawable.game_btn_darkblue,
            buttonText = "랜덤 찾기",
            iconImage = R.drawable.game_icon_random,
            // 클릭하면 랜덤 찾는중 화면 이동
            onClick = {
                // 랜덤 매칭중 페이지로 이동
                val intent = Intent(context, Matching::class.java)
                context.startActivity(intent)
            }
        )
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

            // 코드 생성
            Box(
                modifier = Modifier.padding(bottom = 20.dp)
            ){
                CodeGenerationSection(
                    generatedCode = code,
                    onGenerateClick = {
                        // 여기에 코드 생성 로직 추가
                        code = "TK38NBBF" // 임시로 하드코딩
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
    val scope = rememberCoroutineScope()

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
                        modifier = Modifier.align(Alignment.CenterStart)
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
    val maxLength = 8

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

// 메인 3개 버튼 양식
@Composable
fun CustomButton(
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

// 선택한 텍스트 색 변경
@Composable
fun SelectableText(
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
