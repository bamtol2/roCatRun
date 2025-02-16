package com.eeos.rocatrun.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.eeos.rocatrun.closet.api.ClosetViewModel
import com.eeos.rocatrun.home.api.HomeViewModel
import com.eeos.rocatrun.login.LoginActivity
import com.eeos.rocatrun.login.data.TokenManager
import com.eeos.rocatrun.login.data.TokenStorage
import com.eeos.rocatrun.socket.SocketHandler
import com.eeos.rocatrun.ui.theme.RoCatRunTheme

class HomeActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val closetViewModel: ClosetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

//        SocketHandler.initialize(this)
//        SocketHandler.connect()

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        requestPermissions()
        checkAndRefreshToken()

//        val token = TokenStorage.getAccessToken(this)
//        homeViewModel.fetchHomeInfo(token)
//        closetViewModel.fetchAllItems(token)
//
//        setContent {
//            RoCatRunTheme {
//                HomeScreen(homeViewModel = homeViewModel)
//            }
//        }
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
        if (permissions.any {
                ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
    }

    private fun checkAndRefreshToken(){
        TokenManager.refreshTokensIfNeeded(this){isRefreshSuccessful ->
            if(isRefreshSuccessful){
                val token = TokenStorage.getAccessToken(this)

                SocketHandler.initialize(this)
                SocketHandler.connect()


                homeViewModel.fetchHomeInfo(token)
                closetViewModel.fetchAllInventory(token)

                setContent {
                    RoCatRunTheme {
                        HomeScreen(homeViewModel = homeViewModel)
                    }
                }

            }else {
                Log.e("HomeActivity", "토큰이 만료되었거나 갱신에 실패하여 로그인 화면으로 이동합니다.")
                navigateToLogin()
            }


        }

    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            putExtra("message", "세션이 만료되었습니다. 다시 로그인해주세요.")
        }
        startActivity(intent)
        finish()
    }
}
