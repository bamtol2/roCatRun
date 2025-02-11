package com.eeos.rocatrun.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import com.eeos.rocatrun.home.api.HomeViewModel
import com.eeos.rocatrun.login.data.TokenStorage
import com.eeos.rocatrun.socket.SocketHandler
import com.eeos.rocatrun.ui.theme.RoCatRunTheme

class HomeActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        SocketHandler.initialize(this)
        SocketHandler.connect()

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val token = TokenStorage.getAccessToken(this)
        val authorization = "Bearer $token"
        homeViewModel.fetchHomeInfo(authorization)
//        homeViewModel.fetchHomeInfo()

        setContent {
            RoCatRunTheme {
                HomeScreen(homeViewModel = homeViewModel)
            }
        }
    }
}
