package com.example.knuverse

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.knuverse.databinding.ActivityDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Locale

private val db = FirebaseFirestore.getInstance()

class HorizontalImageAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<HorizontalImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(400, 400) // 각 이미지의 크기
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        return ImageViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
      
        // Glide를 사용하여 이미지를 로드
        Glide.with(holder.imageView.context)
            .load(imageUrl)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = imageUrls.size
}

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 특정 문서의 ID로부터 데이터 가져오기
        val documentId = intent.getStringExtra("DOCUMENT_ID") ?: return
        val selectedLanguage = intent.getStringExtra("SELECTED_LANGUAGE") ?: "ko" // 기본값은 한국어
        loadDocumentData(documentId, selectedLanguage)

        // WindowInsets 적용
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Firestore에서 특정 ID의 문서에서 데이터 가져오는 함수
    private fun loadDocumentData(documentId: String, selectedLanguage: String) {
        val collectionName = if (selectedLanguage == "en") "Partnerships_en" else "Partnerships"

        db.collection(collectionName).document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val title = document.getString("title") ?: "제목 없음"
                    val content = document.getString("content") ?: "내용 없음"
                    // 문자열로 저장된 startDate와 endDate 가져오기
                    val startDate = document.getString("startDate") ?: "시작 날짜 없음"
                    val endDate = document.getString("endDate") ?: "종료 날짜 없음"
                    val imageUrls = document.get("imageUrls")
                        ?.let { it as? List<*> }
                        ?.filterIsInstance<String>() ?: emptyList()

                    // Set data to UI
                    binding.detailTitle.text = title
                    binding.detailContent.text = content
                    binding.detailDate.text = "$startDate ~ $endDate"

                    if (imageUrls.isNotEmpty()) {
                        setupRecyclerView(imageUrls)
                    } else {
                        Toast.makeText(this, "이미지가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "문서가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firebase", "Error getting document", e)
                Toast.makeText(this, "데이터를 가져오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    // RecyclerView 설정 함수
    private fun setupRecyclerView(imageUrls: List<String>) {
        val recyclerView = findViewById<RecyclerView>(R.id.horizontal_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = HorizontalImageAdapter(imageUrls)
    }
}