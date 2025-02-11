package com.eeos.rocatrun.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.eeos.rocatrun.socket.SocketHandler
import com.eeos.rocatrun.ui.theme.RoCatRunTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        SocketHandler.initialize(this)
        SocketHandler.connect()

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            RoCatRunTheme {
                HomeScreen()
            }
        }
    }
}
