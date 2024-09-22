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
    private var selectedCategory: String? = null // 선택된 카테고리

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Spinner 설정
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.language_array,
            R.layout.custom_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languageSpinner.adapter = adapter

        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedLanguage = if (position == 0) "ko" else "en"
                loadFragment()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 카테고리 버튼 클릭 설정
        binding.categoryButton00.setOnClickListener { onCategorySelected("restaurant") }
        binding.categoryButton01.setOnClickListener { onCategorySelected("cafe") }
        binding.categoryButton02.setOnClickListener { onCategorySelected("hospital") }
        binding.categoryButton03.setOnClickListener { onCategorySelected("travel") }
        binding.categoryButton04.setOnClickListener { onCategorySelected("education") }

        setSupportActionBar(binding.toolbar)

        // 처음 화면이 시작될 때 프래그먼트 로드
        if (savedInstanceState == null) {
            loadFragment()
        }

        // 'tbButton' 클릭 이벤트 - RequestActivity로 이동
        binding.tbButton.setOnClickListener {
            startActivity(Intent(this, RequestActivity::class.java))
        }

        // 북마크 진입 버튼 클릭 이벤트 - BookmarkActivity로 이동
        binding.bookmarkPageButton.setOnClickListener {
            val intent = Intent(this, BookmarkActivity::class.java)
            startActivity(intent)
        }

        // 시스템 바 패딩 적용
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // 카테고리 선택 처리 함수
    private fun onCategorySelected(category: String) {
        if (selectedCategory == category) {
            selectedCategory = null // 이미 선택된 카테고리면 해제
            resetCategoryButton()
        } else {
            selectedCategory = category
            updateCategoryButtonImages(category)
        }
        loadFragment() // 선택된 카테고리에 맞는 프래그먼트 로드
    }

    // 카테고리 버튼 이미지 업데이트 함수
    private fun updateCategoryButtonImages(selectedCategory: String) {
        resetCategoryButton() // 모든 카테고리 텍스트 숨기기

        // 선택된 카테고리에 맞게 버튼 이미지 변경
        binding.categoryButton00.setImageResource(if (selectedCategory == "restaurant") R.drawable.tab_select_restaurant else R.drawable.tab_restaurant)
        binding.categoryButton01.setImageResource(if (selectedCategory == "cafe") R.drawable.tab_select_cafe else R.drawable.tab_cafe)
        binding.categoryButton02.setImageResource(if (selectedCategory == "hospital") R.drawable.tab_select_hospital else R.drawable.tab_hospital)
        binding.categoryButton03.setImageResource(if (selectedCategory == "travel") R.drawable.tab_select_travel else R.drawable.tab_travel)
        binding.categoryButton04.setImageResource(if (selectedCategory == "education") R.drawable.tab_select_education else R.drawable.tab_education)

        // 선택된 카테고리의 텍스트만 보이기
        when (selectedCategory) {
            "restaurant" -> binding.categoryText00.visibility = View.VISIBLE
            "cafe" -> binding.categoryText01.visibility = View.VISIBLE
            "hospital" -> binding.categoryText02.visibility = View.VISIBLE
            "travel" -> binding.categoryText03.visibility = View.VISIBLE
            "education" -> binding.categoryText04.visibility = View.VISIBLE
        }
    }

    // 모든 카테고리 버튼 리셋 함수
    private fun resetCategoryButton() {
        binding.categoryText00.visibility = View.INVISIBLE
        binding.categoryText01.visibility = View.INVISIBLE
        binding.categoryText02.visibility = View.INVISIBLE
        binding.categoryText03.visibility = View.INVISIBLE
        binding.categoryText04.visibility = View.INVISIBLE

        binding.categoryButton00.setImageResource(R.drawable.tab_restaurant)
        binding.categoryButton01.setImageResource(R.drawable.tab_cafe)
        binding.categoryButton02.setImageResource(R.drawable.tab_hospital)
        binding.categoryButton03.setImageResource(R.drawable.tab_travel)
        binding.categoryButton04.setImageResource(R.drawable.tab_education)
    }

    // 선택된 언어와 카테고리에 맞는 프래그먼트 로드
    private fun loadFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, RecyclerFragment.newInstance(selectedLanguage, selectedCategory))
            .commit()
    }
}