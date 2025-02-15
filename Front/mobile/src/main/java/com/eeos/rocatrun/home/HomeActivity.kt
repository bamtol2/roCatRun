package com.eeos.rocatrun.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.eeos.rocatrun.closet.api.ClosetViewModel
import com.eeos.rocatrun.home.api.HomeViewModel
import com.eeos.rocatrun.login.data.TokenStorage
import com.eeos.rocatrun.socket.SocketHandler
import com.eeos.rocatrun.ui.theme.RoCatRunTheme

class HomeActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val closetViewModel: ClosetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        SocketHandler.initialize(this)
        SocketHandler.connect()

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val token = TokenStorage.getAccessToken(this)
        homeViewModel.fetchHomeInfo(token)
        closetViewModel.fetchAllItems(token)

        setContent {
            RoCatRunTheme {
                HomeScreen(homeViewModel = homeViewModel)
            }
        }
        requestPermissions()
    }
    fun requestPermissions() {
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
}
