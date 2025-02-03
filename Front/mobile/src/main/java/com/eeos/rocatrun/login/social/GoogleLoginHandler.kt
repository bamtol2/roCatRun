package com.eeos.rocatrun.login.social

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.eeos.rocatrun.R

class GoogleLoginHandler(private val context: Context) {

    private lateinit var googleSignInClient: GoogleSignInClient

    // 초기화
    fun initGoogleLogin() {
        val clientId = context.getString(R.string.google_client_id)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    // 로그인 Intent 실행
    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    // 로그인 결과 처리
    fun handleSignInResult(task: Task<GoogleSignInAccount>): GoogleSignInAccount? {
        return if (task.isSuccessful) {
            task.result
        } else {
            null
        }
    }

    // 로그아웃
    fun signOut() {
        googleSignInClient.signOut()
    }
}