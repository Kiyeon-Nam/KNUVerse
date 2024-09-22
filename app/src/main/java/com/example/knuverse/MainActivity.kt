package com.example.knuverse

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.knuverse.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedLanguage: String = "ko" // 기본 언어 설정 (한국어)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Spinner에 대한 ArrayAdapter 생성
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.language_array,
            R.layout.custom_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languageSpinner.adapter = adapter

        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedLanguage = if (position == 0) "ko" else "en"

                // 언어 변경 시 RecyclerFragment 갱신
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, RecyclerFragment.newInstance(selectedLanguage))
                    .commit()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RecyclerFragment.newInstance(selectedLanguage))
                .commit()
        }

        binding.tbButton.setOnClickListener {
            startActivity(Intent(this, RequestActivity::class.java))
            
        // 북마크 진입 버튼 이벤트
        binding.bookmarkPageButton.setOnClickListener {
            val intent = Intent(this, BookmarkActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
            }
        }
    }
}