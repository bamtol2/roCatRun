package com.eeos.rocatrun.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.eeos.rocatrun.R
import com.eeos.rocatrun.login.LoginButton
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import androidx.compose.ui.platform.LocalContext

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    // 로그로 상태 변경 확인
    Log.d("LoginScreen", "showDialog: $showDialog")  // 상태 확인 로그 추가

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
            Image(
                painter = rememberAsyncImagePainter(R.drawable.login_bg_earth),
                contentDescription = "Earth",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            Image(
                painter = rememberAsyncImagePainter(R.drawable.all_img_whitecat),
                contentDescription = "Cat on Earth",
                modifier = Modifier
                    .width(136.dp)
                    .height(136.dp)
                    .offset(y = -(160).dp, x = 10.dp),
                contentScale = ContentScale.Fit
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
                    showDialog = true
                }
            )
            LoginButton(
                text = "네이버 로그인",
                borderColor = Color(0xFF00C73C),
                backgroundColor = Color(0x4A00C73C),
                iconResId = R.drawable.login_icon_naver,
                onClick = {
                    showDialog = true }
            )
            LoginButton(
                text = "구글 로그인",
                borderColor = Color(0xFFFFFFFF),
                backgroundColor = Color(0x4AFFFFFF),
                iconResId = R.drawable.login_icon_google,
                onClick = {
                    showDialog = true }
            )
        }
    }

    // 모달 표시
    if (showDialog) {
        UserInfoDialog(
            onDismiss = { showDialog = false },
            onConfirm = { showDialog = false },
            profileImageResId = R.drawable.login_img_profile,  // 프로필 이미지 리소스
            borderImageResId = R.drawable.login_bg_greenmodal, // 모달 테두리 이미지 리소스
            okButtonImageResId = R.drawable.login_btn_ok,   // OK 버튼 이미지 리소스

        )
    }

}

@Composable
fun UserInfoDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    profileImageResId: Int,
    borderImageResId: Int,
    okButtonImageResId: Int
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(Color.Transparent)
        ) {
            // 테두리 이미지
            Image(
                painter = painterResource(id = borderImageResId),
                contentDescription = "Dialog Border",
                modifier = Modifier
                    .width(600.dp)
                    .height(500.dp),
                contentScale = ContentScale.FillBounds
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
            ) {
                // "유저 정보 입력" 텍스트
                Text(
                    text = "유저 정보 입력",
                    style = TextStyle(
                        fontSize = 26.sp,
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.neodgm)),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .offset(y = 50.dp)

                )

                // 프로필 이미지
                Image(
                    painter = painterResource(id = profileImageResId),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .offset(y = 60.dp)
                        .size(100.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(40.dp))
                // 닉네임 입력 및 중복 확인 버튼
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF1E1E1E))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "ssafy",  // 임시 텍스트
                            color = Color.White,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.neodgm))
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // 네모 모양의 중복 확인 버튼
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF00FFCC), shape = RoundedCornerShape(8.dp))
                            .padding(vertical = 10.dp, horizontal = 16.dp)
                            .clickable { /* 중복 확인 로직 작성해야함*/ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "중복 확인",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.neodgm))
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // OK 버튼
                Image(
                    painter = painterResource(id = okButtonImageResId),
                    contentDescription = "OK Button",
                    modifier = Modifier
                        .width(130.dp)
                        .height(70.dp)
                        .offset(y = 40.dp)
                        .clickable(onClick = onConfirm) // ok 누르면 메인 페이지로 이동하도록 해야함
                )
            }
        }
    }
}

