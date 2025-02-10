package com.eeos.rocatrun.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.eeos.rocatrun.home.api.HomeViewModel
import com.eeos.rocatrun.login.data.TokenStorage
import com.eeos.rocatrun.socket.SocketHandler
import com.eeos.rocatrun.ui.theme.RoCatRunTheme

class HomeActivity : ComponentActivity() {
//    private lateinit var token: String

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

//        token = TokenStorage.getAccessToken(applicationContext) ?: ""

//        homeViewModel.fetchHomeInfo(token)
        homeViewModel.fetchHomeInfo()

        setContent {

            // 소켓 초기화, 연결
            SocketHandler.initialize()
            SocketHandler.connect()

            RoCatRunTheme {
                HomeScreen(homeViewModel = homeViewModel)
            }
        }
    }
}
