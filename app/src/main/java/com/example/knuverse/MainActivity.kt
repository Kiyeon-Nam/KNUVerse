package com.example.knuverse

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.knuverse.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 바인딩 객체 획득
        binding = ActivityMainBinding.inflate(layoutInflater)
        // 액티비티 화면 출력
        setContentView(binding.root)

        // Spinner에 대한 ArrayAdapter 생성
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.language_array,       // 스피너 항목 배열
            android.R.layout.simple_spinner_item // 기본 스피너 레이아웃
        )

        // 드롭다운을 위한 레이아웃 설정
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Spinner에 어댑터 연결
        binding.languageSpinner.adapter = adapter

        // Spinner 선택 리스너 설정
        binding.languageSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // 선택된 아이템을 토스트 메시지로 표시
                val selectedItem = parent?.getItemAtPosition(position).toString()
                Toast.makeText(this@MainActivity, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 선택된 항목이 없을 때 처리할 내용
            }
        }

        // 액션바의 내용을 툴바에 적용
        setSupportActionBar(binding.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}