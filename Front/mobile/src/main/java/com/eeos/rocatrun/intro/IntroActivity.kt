package com.eeos.rocatrun.intro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.eeos.rocatrun.ui.theme.RoCatRunTheme
import com.google.android.play.integrity.internal.f
import com.google.android.play.integrity.internal.s

class IntroActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            var showIntro by remember { mutableStateOf(false) }

            RoCatRunTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showIntro) {
                        IntroScreen(onClose = { finish() })
                    } else {
                        StoryScreen(onFollowClick = { showIntro = true })
                    }
                }
            }
        }

    }
}

