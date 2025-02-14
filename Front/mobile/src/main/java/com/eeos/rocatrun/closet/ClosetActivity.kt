package com.eeos.rocatrun.closet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.eeos.rocatrun.closet.api.ClosetViewModel
import com.eeos.rocatrun.game.LoadingScreen
import com.eeos.rocatrun.login.data.TokenStorage
import com.eeos.rocatrun.ui.theme.RoCatRunTheme

class ClosetActivity : ComponentActivity() {

    private val closetViewModel: ClosetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                Color.Transparent.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.dark(
                Color.Transparent.toArgb()
            )
        )

        val token = TokenStorage.getAccessToken(this)
        closetViewModel.fetchAllItems(token)

        setContent {
            RoCatRunTheme(
                darkTheme = true
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ClosetScreen(closetViewModel = closetViewModel)
                }
            }
        }
    }
}