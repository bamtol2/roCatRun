package com.eeos.rocatrun.login

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.eeos.rocatrun.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.gif.AnimatedImageDecoder
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.launch
import com.eeos.rocatrun.home.HomeActivity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import com.eeos.rocatrun.login.data.LoginResponse
import com.eeos.rocatrun.login.data.TokenStorage
import com.eeos.rocatrun.login.social.KakaoWebViewLoginActivity
import com.eeos.rocatrun.login.social.NaverWebViewLoginActivity
import com.eeos.rocatrun.login.social.GoogleWebViewLoginActivity
import com.eeos.rocatrun.login.util.NicknameCheckHelper
import com.eeos.rocatrun.login.util.Register
import com.eeos.rocatrun.login.util.MessageBox
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.eeos.rocatrun.login.data.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


import okhttp3.internal.wait

// loginRespons = 백엔드에서 받아온 응답
@Composable
fun LoginScreen(modifier: Modifier = Modifier , loginResponse: LoginResponse?) {

    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var userInfo by remember { mutableStateOf(loginResponse) }
    var showMessageBox by remember { mutableStateOf(false) }
    // 응답이 업데이트되면 showDialog를 true로 설정
    LaunchedEffect(Unit) {
        val token = TokenStorage.getAccessToken(context)
        if (token != null) {
            // 캐릭터 정보 조회 API 호출
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.checkMember("Bearer $token")
                }

                if (response.isSuccessful && response.body()?.success == true) {
                    // 회원 정보가 있을 경우 바로 HomeActivity로 이동
                    Log.d("회원 체크" , "1. $response, 2. ${response.body()}")
                    val intent = Intent(context, HomeActivity::class.java)
                    context.startActivity(intent)
                } else {
                    // 회원 정보가 없으면 회원가입 모달 띄우기
                    showDialog = true
                }
            } catch (e: Exception) {
                Log.e("LoginScreen", "회원 정보 조회 중 오류 발생", e)
                showDialog = true
            }
        } else {
            // 토큰이 없으면 회원가입 모달 띄우기
            showDialog = true
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF051330))
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 제목 텍스트
        Text(
            text = "로캣냥",
            style = TextStyle(
                fontSize = 48.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFFFFFFFF),
                fontFamily = FontFamily(Font(R.font.neodgm)),
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier
                .width(210.dp)
                .height(57.dp)
                .offset(y = 30.dp)
        )

        // 중앙 이미지 (지구 및 고양이)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(350.dp)
                .height(370.dp)
        ) {
            GifImage(

                modifier = Modifier.fillMaxSize(),
                gifResId = R.drawable.login_gif_earth

            )
            GifImage(
                modifier = Modifier
                    .width(136.dp)
                    .height(136.dp)
                    .offset(y = -(160).dp, x = 10.dp),
                gifResId = R.drawable.login_gif_whitecat
            )

        }

        // 로그인 버튼들
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (-40).dp)
        ) {
            LoginButton(
                text = "카카오 로그인",
                borderColor = Color(0xFFFFEB3C),
                backgroundColor = Color(0x4AFFEB3C),
                iconResId = R.drawable.login_icon_kakao,
                onClick = {
                    val intent = Intent(context, KakaoWebViewLoginActivity::class.java)
                    context.startActivity(intent)


                }
            )
            LoginButton(
                text = "네이버 로그인",
                borderColor = Color(0xFF00C73C),
                backgroundColor = Color(0x4A00C73C),
                iconResId = R.drawable.login_icon_naver,
                onClick = {
                    val intent = Intent(context, NaverWebViewLoginActivity::class.java)
                    context.startActivity(intent)
                }
            )
            LoginButton(
                text = "구글 로그인",
                borderColor = Color(0xFFFFFFFF),
                backgroundColor = Color(0x4AFFFFFF),
                iconResId = R.drawable.login_icon_google,
                onClick = {
                    val intent = Intent(context, GoogleWebViewLoginActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }
    }

    // 모달 표시(플래그가 트루이고 유저정보를 받아왔을 때만)
    if (showDialog && userInfo != null) {
        Log.i("로그인 스크린", "유저 정보 입력 모달 켜짐 $userInfo")
        UserInfoDialog(
            userInfo = userInfo,
            onDismiss = { showDialog = false },
            onShowMessageBox = { showMessageBox = true },  // MessageBox 상태 업데이트
            profileImageResId = R.drawable.login_img_profile,  // 프로필 이미지 리소스
            borderImageResId = R.drawable.login_bg_greenmodal, // 모달 테두리 이미지 리소스
            okButtonImageResId = R.drawable.login_btn_ok,   // OK 버튼 이미지 리소스

        )
    }
    // MessageBox 표시
    // MessageBox 호출 부분
    if (showMessageBox) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            MessageBox(
                imageResId = R.drawable.login_img_check,
                message = "회원가입에 성공하였습니다!",
                modifier = Modifier
                    .width(600.dp)
                    .height(600.dp)
            )
        }
    }


}

@Composable
fun UserInfoDialog(
    userInfo : LoginResponse?,
    onDismiss: () -> Unit,
    onShowMessageBox : () -> Unit,
    profileImageResId: Int,
    borderImageResId: Int,
    okButtonImageResId: Int
) {
    // 사용자 정보가 null이 아닌 경우만 모달 표시
    if (userInfo == null) return
    // 사용자 닉네임

    var nicknameStatusMessage by remember { mutableStateOf("") }
    var showNicknameAlert by remember { mutableStateOf(false) } // 경고 알림 표시 여부
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    // 회원가입 시 입력 받는 정보
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }



    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = borderImageResId),
                contentDescription = "Dialog Border",
                modifier = Modifier
                    .width(700.dp)
                    .height(710.dp),
                contentScale = ContentScale.FillBounds
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "유저 정보 입력",
                    style = TextStyle(
                        fontSize = 35.sp,
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.neodgm)),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.offset(y = 50.dp)
                )

                Image(
                    painter = painterResource(id = profileImageResId),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .offset(y = 50.dp)
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "닉네임",
                    style = TextStyle(
                        fontSize = 22.sp,
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.neodgm)),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.offset(y = 50.dp)
                )

                Spacer(modifier = Modifier.height(35.dp))

                // 닉네임 입력 필드
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = nickname,
                        onValueChange = { newNickname ->
                            if (newNickname.length <= 8) {
                                nickname = newNickname
                            } else {
                                showNicknameAlert = true // 경고 알림 표시
                            }
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.neodgm))
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.White, RoundedCornerShape(8.dp)),
                        placeholder = {
                            Text(
                                text = "닉네임을 입력하세요",
                                color = Color.Gray,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(Font(R.font.neodgm))
                                )
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFF444359),
                            focusedContainerColor = Color(0xFF444359)
                        )
                    )

                    // 중복 확인 버튼
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 10.dp)
                            .background(Color(0xFF00FFCC), shape = RoundedCornerShape(8.dp))
                            .padding(vertical = 8.dp, horizontal = 12.dp)
                            .clickable {  coroutineScope.launch {
                                val token = TokenStorage.getAccessToken(context)
                                if (token != null){
                                    val isDuplicate = NicknameCheckHelper.checkNicknameAvailability(nickname, token)
                                    nicknameStatusMessage = when (isDuplicate) {
                                        true -> "중복된 닉네임입니다."
                                        false -> "사용 가능한 닉네임입니다."
                                        else -> "닉네임 중복 확인에 실패했습니다."
                                    }
                                }
                            }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "중복 확인",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.neodgm))

                            ),

                        )
                    }
                }

                // 중복 확인 상태 메시지 표시
                // 상태 메시지 표시
                Text(
                    text = nicknameStatusMessage,
                    color = if (nicknameStatusMessage.contains("중복")) Color.Red else Color.Green,
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.offset(y = (-10).dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // "신체 정보" 텍스트와 입력 박스 그룹
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.offset(y = (-20).dp)  // 원하는 만큼 위로 이동
                    ) {
                        Text(
                            text = "신체 정보",
                            style = TextStyle(
                                fontSize = 22.sp,
                                color = Color.White,
                                fontFamily = FontFamily(Font(R.font.neodgm)),
                                textAlign = TextAlign.Center
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // 신체 정보 입력 박스
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.White, RoundedCornerShape(12.dp))
                                .background(Color(0xFF444359), RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // 나이 입력 필드와 성별 아이콘
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            TextField(
                                                value = age,
                                                onValueChange = { age = it },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                singleLine = true,
                                                trailingIcon = {
                                                    Text(
                                                        text = "세",
                                                        style = TextStyle(
                                                            fontSize = 14.sp,
                                                            color = Color.White,
                                                            fontFamily = FontFamily(Font(R.font.neodgm))
                                                        )
                                                    )
                                                },
                                                colors = TextFieldDefaults.colors(
                                                    unfocusedContainerColor = Color(0xFF444359),
                                                    focusedContainerColor = Color(0xFF444359),
                                                    unfocusedIndicatorColor = Color.Transparent,
                                                    focusedIndicatorColor = Color.Transparent
                                                ),
                                                textStyle = TextStyle(
                                                    fontSize = 16.sp,
                                                    color = Color.White,
                                                    fontFamily = FontFamily(Font(R.font.neodgm))
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            // 밑줄 추가
                                            HorizontalDivider(thickness = 1.dp, color = Color.White)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    // 성별 아이콘
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Image(
                                            painter = painterResource(id = R.drawable.login_icon_male),
                                            contentDescription = "남성 아이콘",
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .border(
                                                    if (gender == "male"){
                                                        BorderStroke(4.dp, Color.Black)
                                                    }else{
                                                      BorderStroke(0.3.dp,Color.Cyan)
                                                    },
                                                    shape = CircleShape

                                                )
                                                .clickable { gender = "male" }
                                        )

                                        Image(
                                            painter = painterResource(id = R.drawable.login_icon_female),
                                            contentDescription = "여성 아이콘",
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .border(
                                                    if (gender == "female"){
                                                        BorderStroke(4.dp, Color.Black)
                                                    }else{
                                                        BorderStroke(0.3.dp,Color.Cyan)
                                                    },
                                                    shape = CircleShape

                                                )
                                                .clickable {gender = "female" }
                                        )
                                    }
                                }

                                // 키와 몸무게 입력 필드
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            TextField(
                                                value = height,
                                                onValueChange = { height = it },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                singleLine = true,
                                                trailingIcon = {
                                                    Text(
                                                        text = "cm",
                                                        style = TextStyle(
                                                            fontSize = 14.sp,
                                                            color = Color.White,
                                                            fontFamily = FontFamily(Font(R.font.neodgm))
                                                        )
                                                    )
                                                },
                                                colors = TextFieldDefaults.colors(
                                                    unfocusedContainerColor = Color(0xFF444359),
                                                    focusedContainerColor = Color(0xFF444359),
                                                    unfocusedIndicatorColor = Color.Transparent,
                                                    focusedIndicatorColor = Color.Transparent
                                                ),
                                                textStyle = TextStyle(
                                                    fontSize = 16.sp,
                                                    color = Color.White,
                                                    fontFamily = FontFamily(Font(R.font.neodgm))
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            // 밑줄 추가
                                            HorizontalDivider(thickness = 1.dp, color = Color.White)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            TextField(
                                                value = weight,
                                                onValueChange = { weight = it },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                singleLine = true,
                                                trailingIcon = {
                                                    Text(
                                                        text = "kg",
                                                        style = TextStyle(
                                                            fontSize = 14.sp,
                                                            color = Color.White,
                                                            fontFamily = FontFamily(Font(R.font.neodgm))
                                                        )
                                                    )
                                                },
                                                colors = TextFieldDefaults.colors(
                                                    unfocusedContainerColor = Color(0xFF444359),
                                                    focusedContainerColor = Color(0xFF444359),
                                                    unfocusedIndicatorColor = Color.Transparent,
                                                    focusedIndicatorColor = Color.Transparent
                                                ),
                                                textStyle = TextStyle(
                                                    fontSize = 16.sp,
                                                    color = Color.White,
                                                    fontFamily = FontFamily(Font(R.font.neodgm))
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            // 밑줄 추가
                                            HorizontalDivider(thickness = 1.dp, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }




                Image(
                    painter = painterResource(id = okButtonImageResId),
                    contentDescription = "OK Button",
                    modifier = Modifier
                        .width(150.dp)
                        .height(70.dp)
                        .offset(y = (-25).dp)
                        .clickable {
                             // 회원가입 API 호출
                            coroutineScope.launch {
                                val token = TokenStorage.getAccessToken(context)
                                if (token != null){
                                    val registerSuccess = Register.registerCharacter(
                                        context,
                                        nickname,
                                        token,
                                        age.toIntOrNull()?:0,
                                        weight.toIntOrNull()?:0,
                                        height.toIntOrNull()?:0,
                                        gender)
                                    if (registerSuccess){
                                        onShowMessageBox()
                                        onDismiss()
                                        Log.i("회원가입 성공", "회원가입 성공")

                                    }else{

                                    }
                                }
                            }


                        }
                )


            }
        }

    }



    // 닉네임 경고 알림 다이얼로그
    if (showNicknameAlert) {
        AlertDialog(
            onDismissRequest = { showNicknameAlert = false },
            confirmButton = {
                Text(
                    text = "확인",
                    modifier = Modifier
                        .clickable { showNicknameAlert = false }
                        .padding(8.dp),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    text = "닉네임은 최대 8글자까지 입력 가능합니다.",
                    style = TextStyle(fontSize = 16.sp, color = Color.Black)
                )
            }
        )
    }

}


@Composable
fun GifImage(modifier: Modifier = Modifier, gifResId: Int) {
    val context = LocalContext.current

    // Coil3의 GIF 디코더를 적용한 ImageLoader 생성
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                add(AnimatedImageDecoder.Factory(enforceMinimumFrameDelay = true))

            }
            .build()
    }

    // GIF 이미지를 위한 ImageRequest 구성
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(gifResId)
            .crossfade(true)
            .build(),
        imageLoader = imageLoader
    )

    // 이미지 디스플레이
    Image(
        painter = painter,
        contentDescription = "GIF Image",
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}

