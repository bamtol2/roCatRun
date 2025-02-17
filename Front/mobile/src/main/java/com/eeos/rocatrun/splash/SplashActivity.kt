package com.eeos.rocatrun.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.eeos.rocatrun.MainActivity
import com.eeos.rocatrun.R
import com.eeos.rocatrun.shop.ShopScreen
import com.eeos.rocatrun.ui.components.GifImage
import com.eeos.rocatrun.ui.theme.RoCatRunTheme
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            RoCatRunTheme(
                darkTheme = true
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SplashScreen()
                }
            }
        }
    }

    @SuppressLint("RememberReturnType")
    @Preview
    @Composable
    fun SplashScreen() {
//        val context = LocalContext.current

        val alpha = remember { Animatable(0f) }
        LaunchedEffect(key1 = Unit) {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(1300)
            )
            delay(900L)

            Intent(this@SplashActivity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.let { intent ->
                startActivity(intent)
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // 배경 이미지
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha.value),
                painter = painterResource(id = R.drawable.splash_bg_img),
                contentScale = ContentScale.FillBounds,
                contentDescription = "배경"
            )

            // 로고 이미지
            Image(
                modifier = Modifier
                    .width(400.dp)
                    .height(200.dp)
                    .alpha(alpha.value)
                    .align(Alignment.Center),
                painter = painterResource(id = R.drawable.splash_logo_img),
                contentDescription = "로고",
            )

//            val resourceId =
//                context.resources.getIdentifier("balloon_blue_on", "drawable", context.packageName)
//            val imageUrl = if (resourceId != 0) {
//                "android.resource://${context.packageName}/$resourceId"
//            } else {
//                "android.resource://com.eeos.rocatrun/${R.drawable.closet_img_x}"
//            }
//
//            GifImage(
//                modifier = Modifier
//                    .width(400.dp)
//                    .height(200.dp)
//                    .alpha(alpha.value)
//                    .align(Alignment.Center),
//                gifUrl = imageUrl
//            )
        }
    }
}
