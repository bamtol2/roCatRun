package com.eeos.rocatrun.login

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.eeos.rocatrun.login.data.LoginResponse
import com.eeos.rocatrun.login.data.TokenStorage
import com.eeos.rocatrun.login.social.KakaoWebViewLoginActivity
import com.eeos.rocatrun.login.social.NaverWebViewLoginActivity
import com.eeos.rocatrun.login.social.GoogleWebViewLoginActivity
import com.eeos.rocatrun.login.util.NicknameCheckHelper
import com.eeos.rocatrun.login.util.Register
import com.eeos.rocatrun.login.util.MessageBox

// loginRespons = 백엔드에서 받아온 응답
@Composable
fun LoginScreen(modifier: Modifier = Modifier , loginResponse: LoginResponse?) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var userInfo by remember { mutableStateOf(loginResponse) }
    var showMessageBox by remember { mutableStateOf(false) }
    // 응답이 업데이트되면 showDialog를 true로 설정
    LaunchedEffect(userInfo) {
        if (userInfo != null) {
            showDialog = true
            Log.i("로그인 스크린", "유저 정보 모달 표시: $userInfo")
        } else {
            Log.e("로그인 스크린", "리스폰스 null")
        }
    }

        Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF051330))
            .padding(16.dp),
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
            horizontalAlignment = Alignment.CenterHorizontally
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
    var nickname by remember { mutableStateOf("") }
    var nicknameStatusMessage by remember { mutableStateOf("") }
    var showNicknameAlert by remember { mutableStateOf(false) } // 경고 알림 표시 여부
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()



    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        ) {
            Image(
                painter = painterResource(id = borderImageResId),
                contentDescription = "Dialog Border",
                modifier = Modifier
                    .width(700.dp)
                    .height(600.dp),
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
                        .offset(y = 70.dp)
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(80.dp))

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
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                        placeholder = {
                            Text(
                                text = "닉네임을 입력하세요",
                                color = Color.Gray,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(Font(R.font.neodgm))
                                )
                            )
                        }
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
                            )
                        )
                    }
                }

                // 중복 확인 상태 메시지 표시
                // 상태 메시지 표시
                Text(
                    text = nicknameStatusMessage,
                    color = if (nicknameStatusMessage.contains("중복")) Color.Red else Color.Green,
                    style = TextStyle(fontSize = 12.sp)
                )

                Spacer(modifier = Modifier.height(16.dp))



                Image(
                    painter = painterResource(id = okButtonImageResId),
                    contentDescription = "OK Button",
                    modifier = Modifier
                        .width(150.dp)
                        .height(70.dp)
                        .offset(y = 40.dp)
                        .clickable {
//                            val intent = Intent(context, HomeActivity::class.java)
//                            context.startActivity(intent)
                            onDismiss()
                            onShowMessageBox()
                            // 회원가입 API 호출
//                            coroutineScope.launch {
//                                val token = TokenStorage.getAccessToken(context)
//                                if (token != null){
//                                    val registerSuccess = Register.registerCharacter(nickname, token)
//                                    if (registerSuccess){
//                                        Log.i("회원가입 성공", "회원가입 성공")
//
//                                    }else{
//                                        Toast.makeText(context, "닉네임을 다시 입력해주세요",Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                            }


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

