package com.eeos.rocatrun.profile

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eeos.rocatrun.R
import com.eeos.rocatrun.login.LoginActivity
import com.eeos.rocatrun.login.data.TokenStorage
import com.eeos.rocatrun.profile.api.ProfileResponse
import com.eeos.rocatrun.profile.api.ProfileViewModel
import com.eeos.rocatrun.ui.theme.MyFontFamily


@Composable
fun ProfileDialog(onDismiss: () -> Unit, profileData: ProfileResponse?, profileViewModel: ProfileViewModel = viewModel()) {
    val context = LocalContext.current
    val token = TokenStorage.getAccessToken(context)

    var isEditing by remember { mutableStateOf(false) } // 수정 모드 여부

    // 닉네임 관련 변수들
    var nickname by remember { mutableStateOf(profileData?.data?.nickname ?: "") }
    val previousNickname by remember { mutableStateOf(nickname) }
    var isDuplicateChecked by remember { mutableStateOf(false) } // 중복 확인 여부
    val isNicknameValid = profileViewModel.nicknameCheckResult.observeAsState().value ?: false// 닉네임 사용 가능
    val maxLength = 8

    // 정보 수정 텍스트 필드 변수들
    val age = rememberTextFieldState(profileData?.data?.age.toString() ?: "")
    val height = rememberTextFieldState(profileData?.data?.height.toString() ?: "")
    val weight = rememberTextFieldState(profileData?.data?.weight.toString() ?: "")
    val genderOptions = listOf("male", "female")
    val (selectedOption, onOptionSelected) = remember {
        mutableStateOf(
            profileData?.data?.gender ?: "male"
        )
    }
    val maleImage = painterResource(id = R.drawable.profile_icon_male)
    val femaleImage = painterResource(id = R.drawable.profile_icon_female)

    // 저장 관련 변수들
    var showToast by remember { mutableStateOf(false) }
    val isButtonEnabled = isEditing && (if (nickname != previousNickname) {
        isNicknameValid && isDuplicateChecked
    } else {
        true
    })

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current


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

                // 닉네임 정보
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "닉네임",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (isDuplicateChecked) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 5.dp),
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

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = nickname,
                            onValueChange = { newNickname ->
                                if (newNickname.length <= maxLength) {
                                    nickname = newNickname
                                }
                                if (isDuplicateChecked) {
                                    isDuplicateChecked = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(vertical = 0.dp)
                                .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                                .focusRequester(focusRequester),
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = MyFontFamily
                            ),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = if (isEditing) Color(0xBDACA8A8) else Color(
                                    0xBD555151
                                ),
                                focusedContainerColor = if (isEditing) Color(0xBDACA8A8) else Color(
                                    0xBD555151
                                ),
                                disabledContainerColor = Color(0xBD555151),
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                            ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = isEditing,
                            singleLine = true,
                            keyboardActions = KeyboardActions(onDone = { // 완료 키 클릭 시 키보드 안 사라짐 issue
                                focusManager.clearFocus(force = true)
                            }),
                        )

                        // 중복 확인 버튼
                        if (isEditing) {
                            Button(
                                onClick = {
                                    if (nickname != previousNickname) {
                                        profileViewModel.checkNicknameAvailability(token, nickname)
                                        isDuplicateChecked = true
                                    } },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Text(
                                    text = "중복 확인",
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
                }
                Spacer(modifier = Modifier.height(10.dp))

                // 소셜 연동 정보
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "소셜 연동 정보",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    value = profileData?.data?.socialType ?: "",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(vertical = 0.dp)
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = MyFontFamily
                    ),
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = Color(0xBD555151),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = false
                )
                Spacer(modifier = Modifier.height(10.dp))

                // 신체 정보
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "신체 정보",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                        .background(Color(0xBD555151)),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Row {
                            // 나이 입력
                            CustomTextField(
                                state = age,
                                label = "나이",
                                unit = "세",
                                isEditing = isEditing,
                            )
                            Spacer(modifier = Modifier.width(15.dp))

                            // 성별 선택
                            Row(
                                modifier = Modifier
                                    .selectableGroup()
                                    .width(110.dp)
                                    .height(32.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                genderOptions.forEach { text ->
                                    Row(
                                        Modifier
                                            .selectable(
                                                selected = (text == selectedOption),
                                                onClick = {
                                                    if (isEditing) {
                                                        onOptionSelected(text)
                                                    }
                                                },
                                                role = Role.RadioButton,
                                                enabled = isEditing
                                            ),
                                    ) {
                                        Image(
                                            painter = if (text == "male") maleImage else femaleImage,
                                            contentDescription = text,
                                            modifier = Modifier
                                                .size(32.dp)
                                                .border(
                                                    width = if (text == selectedOption) 2.dp else 0.dp,
                                                    color = when {
                                                        isEditing && text == selectedOption -> Color(
                                                            0xFF12D9C8
                                                        ) // 선택된 상태 색
                                                        !isEditing -> Color(0xFF75A09F)  // Disabled 상태 색
                                                        else -> Color.Transparent // 선택되지 않은 상태 색
                                                    },
                                                    shape = CircleShape
                                                )
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        Row {
                            // 키 입력
                            CustomTextField(
                                state = height,
                                label = "키",
                                unit = "cm",
                                unitSize = 18,
                                isEditing = isEditing,
                                labelPadding = 20,
                            )
                            Spacer(modifier = Modifier.width(15.dp))

                            // 무게 입력
                            CustomTextField(
                                state = weight,
                                label = "무게",
                                unit = "kg",
                                unitSize = 16,
                                isEditing = isEditing,
                            )
                        }
                    }

                }

                // 회원탈퇴 및 정보수정 버튼
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(x = 10.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    CustomTextButton(
                        text = "회원 탈퇴",
                        textColor = Color(0xFFFD2727),
                        onClick = { /* 회원 탈퇴 로직 */ }
                    )

                    CustomTextButton(
                        text = "정보 수정",
                        textColor = Color(0xFFFFFFFF),
                        onClick = { isEditing = true }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // 로그아웃 버튼
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxSize(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xBDD7D7D7)),
                        shape = RoundedCornerShape(8.dp),
                        onClick = {
                            val authorization = "Bearer $token"
                            profileViewModel.fetchLogout(authorization)
                            TokenStorage.clearTokens(context)
                            // 로그인 화면으로 이동
                            val intent = Intent(context, LoginActivity::class.java)
                            context.startActivity(intent)
                        },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.profile_icon_logout),
                                contentDescription = "logout Icon",
                                modifier = Modifier.size(30.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "로그아웃",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = MyFontFamily,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 저장 및 취소 버튼
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // 저장 버튼
                    Button(
                        onClick = { saveNickname() },
                        enabled = isButtonEnabled,
                        modifier = Modifier
                            .border(2.dp, Color(0xFF00FFCC), RoundedCornerShape(15.dp)),
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
                                color = Color(0xFF00FFCC),
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


@Composable
fun CustomTextField(
    state: TextFieldState,
    label: String,    // '나이', '키', '무게'
    unit: String,     // '세', 'cm', 'kg'
    unitSize: Int = 14,
    isEditing: Boolean,
    height: Dp = 30.dp,
    width: Dp = 110.dp,
    maxLength: Int = 3,
    innerWidth: Int = 50,
    labelPadding: Int = 8,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val textColor = when {
        !isEditing -> Color(0xBDACA8A8) // Disabled 상태
        interactionSource.collectIsFocusedAsState().value -> Color(0xFFFFFFFF) // Focused 상태
        else -> Color(0xFFA7B5BD) // 일반 상태
    }

    BasicTextField(
        state = state,
        enabled = isEditing,
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        inputTransformation = InputTransformation.maxLength(maxLength),
        modifier = Modifier
            .height(height)
            .width(width),
        textStyle = TextStyle(
            fontFamily = MyFontFamily,
            fontSize = 20.sp,
            color = textColor
        ),
        interactionSource = interactionSource,
        decorator = { innerTextField ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        label,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = labelPadding.dp)
                    )
                    Box(Modifier.width(innerWidth.dp)) { innerTextField() }
                    Text(
                        unit,
                        color = textColor,
                        fontSize = unitSize.sp,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(thickness = 1.dp, color = textColor)
            }
        }
    )
}

@Composable
fun CustomTextButton(
    text: String,
    textColor: Color,
    onClick: () -> Unit,
    fontSize: TextUnit = 16.sp,
) {
    TextButton(onClick = onClick) {
        Text(
            text = text,
            fontSize = fontSize,
            color = textColor,
            style = TextStyle(
                fontFamily = MyFontFamily,
                textDecoration = TextDecoration.Underline
            )
        )
    }
}