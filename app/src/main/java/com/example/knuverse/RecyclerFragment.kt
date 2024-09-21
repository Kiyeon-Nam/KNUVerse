package com.example.knuverse

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.knuverse.databinding.FragmentRecyclerBinding
import com.example.knuverse.databinding.ItemCardBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

private val db = FirebaseFirestore.getInstance()

class MyViewHolder(val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root)

class MyAdapter(val datas: List<DocumentData>) : RecyclerView.Adapter<MyViewHolder>() {

    // 항목 개수 판단
    override fun getItemCount(): Int = datas.size

    // 항목 뷰를 가지는 뷰 홀더를 준비
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    // 각 항목을 구성
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding
        val item = datas[position]

        // 텍스트 설정
        binding.cardHead.text = item.title
        binding.cardText.text = item.content
        binding.cardDate.text = "${item.startDate} ~ ${item.endDate}"

        // 제휴/홍보 상태 설정
        if (item.isPartnership) {
            binding.cardStatus.text = "제휴"
        } else {
            binding.cardStatus.text = "홍보"
        }

        // 이미지가 있으면 첫 번째 이미지 로드
        if (item.imageUrls.isNotEmpty()) {
            loadImageFromUrl(item.imageUrls[0], binding.cardThumbnail)
        } else {
            Toast.makeText(holder.itemView.context, "No images found", Toast.LENGTH_SHORT).show()
        }

        Log.d("test", "item root click : $position")
    }

    // Glide를 사용하여 이미지 로드
    private fun loadImageFromUrl(url: String, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    }
}

// Firestore 문서 데이터를 저장할 데이터 클래스
data class DocumentData(
    val title: String,
    val content: String,
    val startDate: String,
    val endDate: String,
    val isPartnership: Boolean,
    val imageUrls: List<String>
)

class RecyclerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRecyclerBinding.inflate(inflater, container, false)

        // RecyclerView 설정
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)

        // Firestore에서 데이터 한 번만 가져오기
        loadData { datas ->
            binding.recyclerView.adapter = MyAdapter(datas)
        }

        return binding.root
    }

    private fun loadData(callback: (List<DocumentData>) -> Unit) {
        db.collection("Partnerships")
            .get()
            .addOnSuccessListener { result ->
                val datas = mutableListOf<DocumentData>()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                for (document in result) {
                    // Firestore 문서에서 데이터 추출
                    val title = document.getString("title") ?: "제목 없음"
                    val content = document.getString("content") ?: "내용 없음"
                    val startDate = document.getTimestamp("startDate")?.let { dateFormat.format(it.toDate()) } ?: "시작 날짜 없음"
                    val endDate = document.getTimestamp("endDate")?.let { dateFormat.format(it.toDate()) } ?: "종료 날짜 없음"
                    val isPartnership = document.getBoolean("isPartnership") ?: false
                    val imageUrls = document.get("imageUrls") as? List<String> ?: emptyList()

                    // DocumentData 객체로 변환하여 리스트에 추가
                    datas.add(
                        DocumentData(
                            title = title,
                            content = content,
                            startDate = startDate,
                            endDate = endDate,
                            isPartnership = isPartnership,
                            imageUrls = imageUrls
                        )
                    )
                }

                // 콜백을 통해 데이터 전달
                callback(datas)
            }
            .addOnFailureListener { exception ->
                Log.w("Firebase", "Error getting documents.", exception)
                Toast.makeText(requireContext(), "Error loading data", Toast.LENGTH_SHORT).show()
            }
    }
}