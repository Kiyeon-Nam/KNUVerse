package com.example.knuverse

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.knuverse.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 바인딩 객체 획득
        binding = ActivityMainBinding.inflate(layoutInflater)
        // 액티비티 화면 출력
        setContentView(binding.root)

        // UI 요소
        val titleTextView: TextView = findViewById(R.id.titleTextView)
        val contentTextView: TextView = findViewById(R.id.contentTextView)
        val startDateTextView: TextView = findViewById(R.id.startDateTextView)
        val endDateTextView: TextView = findViewById(R.id.endDateTextView)
        val isPartnershipTextView: TextView = findViewById(R.id.isPartnershipTextView)
        val imageView: ImageView = findViewById(R.id.imageView3)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Firestore에서 데이터 가져오기
        db.collection("Partnerships")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("Firebase", "${document.id} => ${document.data}")
                    val title = document.getString("title")
                    val content = document.getString("content")
                    val startDate = document.getTimestamp("startDate")
                    val endDate = document.getTimestamp("endDate")
                    val isPartnership = document.getBoolean("isPartnership")
                    val imageUrls = document.get("imageUrls") as? List<String>

                    // 텍스트 설정
                    titleTextView.text = title
                    contentTextView.text = content
                    startDateTextView.text = startDate?.let { dateFormat.format(it.toDate()) } ?: "No Start Date"
                    endDateTextView.text = endDate?.let { dateFormat.format(it.toDate()) } ?: "No End Date"

                    if (isPartnership == true) {
                        isPartnershipTextView.text = "제휴"
                    } else {
                        isPartnershipTextView.text = "홍보"
                    }

                    // 이미지가 있으면 첫 번째 이미지 로드
                    if (imageUrls != null && imageUrls.isNotEmpty()) {
                        loadImageFromUrl(imageUrls[0], imageView)
                    } else {
                        Toast.makeText(this, "No images found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firebase", "Error getting documents.", exception)
                Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show()
            }
    }

    // Glide를 사용하여 이미지 로드
    private fun loadImageFromUrl(url: String, imageView: ImageView) {
        Glide.with(this)
            .load(url)
            .into(imageView)
    }
}