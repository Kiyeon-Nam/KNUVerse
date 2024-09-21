package com.example.knuverse

import android.app.Application
import com.google.firebase.FirebaseApp

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase 초기화
        FirebaseApp.initializeApp(this)
    }
}