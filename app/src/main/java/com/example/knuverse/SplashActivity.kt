package com.example.knuverse

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // 시간 지연 이후 실행하는 코드
        Handler(Looper.getMainLooper()).postDelayed({

            // 시간 이후 MainActivity로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            // "이전" 버튼 눌러도 돌아오지 않도록 '사용안함'을 위해 처리
            finish()

        }, 2000) // 지연 시간 2초

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}