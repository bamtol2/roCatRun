package com.eeos.rocatrun.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import com.eeos.rocatrun.R
import com.eeos.rocatrun.ui.theme.MyFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ProfileDialog(onDismiss: () -> Unit) {
    var nickname by remember { mutableStateOf("벤츠남") } // 닉네임 초기값
    var isEditing by remember { mutableStateOf(false) } // 수정 모드 여부
    var isDuplicateChecked by remember { mutableStateOf(false) } // 중복 확인 여부
    var isNicknameValid by remember { mutableStateOf(true) } // 닉네임 유효성
    var showToast by remember { mutableStateOf(false) } // Toast 표시 여부
    val isButtonEnabled = isNicknameValid && isDuplicateChecked
    val maxLength = 8 // 닉네임 길이

    val focusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // 중복 확인 처리
    fun checkNickname() {
        isNicknameValid = nickname != "벤츠남"
        isDuplicateChecked = true
    }

    // 저장 버튼 클릭 시 처리
    fun saveNickname() {
        showToast = true
        isEditing = false
        isDuplicateChecked = false
    }


    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(650.dp)
                .padding(0.dp),
            contentAlignment = Alignment.Center,
        ) {
            // 모달 배경 이미지
            Image(
                painter = painterResource(id = R.drawable.home_bg_modal),
                contentDescription = "Modal Background",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .padding(horizontal = 20.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 모달 Title
                Text(
                    text = "유저 정보",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(20.dp))

                // 프로필 이미지
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDE7F98)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.all_img_whitecat),
                        contentDescription = "Profile Image",
                        modifier = Modifier.size(70.dp)
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))

                // 닉네임 정보
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "닉네임",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = nickname,
                            onValueChange = {
                                    newNickname ->
                                if (newNickname.length <= maxLength) {
                                    nickname = newNickname
                                }
                                if (isDuplicateChecked) {
                                    isDuplicateChecked = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                                .focusRequester(focusRequester),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.DarkGray,
                                focusedContainerColor = Color.DarkGray,
                                disabledContainerColor = Color.DarkGray,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent
                            ),
                            enabled = isEditing,
                            singleLine = true,
                            keyboardActions = KeyboardActions(onDone = { // 완료 키 클릭 시 키보드 안 사라짐 issue
                                focusManager.clearFocus(force = true)
                                Log.d("DEBUG", "onDone Triggered")
                            }),
                        )

                        // 수정/중복 확인 버튼
                        Button(
                            onClick = {
                                if (isEditing) {
                                    checkNickname()
                                } else {
                                    isEditing = true
                                    scope.launch {
                                        delay(50)  // 500ms 대기
                                        focusRequester.requestFocus()  // 포커스 요청
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Text(
                                text = if (isEditing) "중복 확인" else "수정",
                                style = TextStyle(
                                    fontFamily = MyFontFamily,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 25.dp)
                ) {
                    if (isDuplicateChecked) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = if (isNicknameValid) "사용 가능한 닉네임입니다" else "중복된 닉네임입니다",
                                color = if (isNicknameValid) Color.Green else Color.Red,
                                style = TextStyle(fontSize = 12.sp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                // 소셜 연동 정보
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "소셜 연동 정보",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    value = "카카오 로그인",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = Color.DarkGray,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    enabled = false
                )

                // 회원 탈퇴 버튼
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { /* 회원 탈퇴 로직 */ }) {
                        Text(
                            text = "회원 탈퇴",
                            color = Color(0xFFFD2727),
                            style = TextStyle(
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))

                // 버튼 영역
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // 저장 버튼
                    Button(
                        onClick = { saveNickname() },
                        enabled = isButtonEnabled,
                        modifier = Modifier
                            .border(2.dp, Color(0xFF36DBEB), RoundedCornerShape(15.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        )
                    ) {
                        Text(
                            text = "저장",
                            style = TextStyle(
                                fontFamily = MyFontFamily,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isButtonEnabled) Color.White else Color(0xFF5F5F5F)
                            )
                        )
                    }

                    // 취소 버튼
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = Color(0xFF36DBEB),
                                shape = RoundedCornerShape(15.dp)
                            ),
                    ) {
                        Text(
                            text = "취소",
                            style = TextStyle(
                                fontFamily = MyFontFamily,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }

                if (showToast) {
                    Toast.makeText(LocalContext.current, "저장되었습니다", Toast.LENGTH_SHORT).show()
                    showToast = false
                }

            }
        }
    }
}
