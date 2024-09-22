package com.example.knuverse

import android.content.Intent
import android.os.Bundle
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

class MyAdapter(private val datas: List<DocumentData>, private val selectedLanguage: String) : RecyclerView.Adapter<MyViewHolder>() {

    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding
        val item = datas[position]

        // 텍스트 설정
        binding.cardHead.text = item.title
        binding.cardText.text = item.content
        binding.cardDate.text = "${item.startDate} ~ ${item.endDate}"

        // 제휴/홍보 상태 설정
        binding.cardStatus.text = if (item.isPartnership) "제휴" else "홍보"

        // 이미지가 있으면 첫 번째 이미지 로드
        if (item.imageUrls.isNotEmpty()) {
            loadImageFromUrl(item.imageUrls[0], binding.cardThumbnail)
        } else {
            Toast.makeText(holder.itemView.context, "No images found", Toast.LENGTH_SHORT).show()
        }

        // 클릭 이벤트 처리 - 카드 배경 클릭 시 DetailActivity로 이동
        binding.cardBackground.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("DOCUMENT_ID", item.documentId) // Document ID를 전달
                putExtra("SELECTED_LANGUAGE", selectedLanguage) // 선택한 언어 추가
            }
            context.startActivity(intent)
        }
    }

    private fun loadImageFromUrl(url: String, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    }
}

// Firestore 문서 데이터를 저장할 데이터 클래스
data class DocumentData(
    val documentId: String,  // Firestore 문서 ID 필드 추가
    val title: String,
    val content: String,
    val startDate: String,
    val endDate: String,
    val isPartnership: Boolean,
    val imageUrls: List<String>
)

class RecyclerFragment : Fragment() {

    private var selectedLanguage: String? = null

    companion object {
        fun newInstance(selectedLanguage: String): RecyclerFragment {
            val fragment = RecyclerFragment()
            val args = Bundle()
            args.putString("selectedLanguage", selectedLanguage)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentRecyclerBinding.inflate(inflater, container, false)
        selectedLanguage = arguments?.getString("selectedLanguage") ?: "ko"

        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        loadData { datas ->
            binding.recyclerView.adapter = MyAdapter(datas, selectedLanguage!!)
        }

        return binding.root
    }

    private fun loadData(callback: (List<DocumentData>) -> Unit) {
        val collectionName = if (selectedLanguage == "en") "Partnerships_en" else "Partnerships"

        db.collection(collectionName)
            .get()
            .addOnSuccessListener { result ->
                val datas = mutableListOf<DocumentData>()

                result.forEach { document ->
                    val documentId = document.id
                    // 문자열로 저장된 startDate와 endDate 가져오기
                    val startDate = document.getString("startDate") ?: "시작 날짜 없음"
                    val endDate = document.getString("endDate") ?: "종료 날짜 없음"
                    val isPartnership = document.getBoolean("isPartnership") ?: false
                    val imageUrls = (document.get("imageUrls") as? List<*>)?.filterIsInstance<String>() ?: emptyList()

                    datas.add(
                        DocumentData(
                            documentId = documentId,
                            title = document.getString("title") ?: "제목 없음",
                            content = document.getString("content") ?: "내용 없음",
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
                Toast.makeText(requireContext(), "Error loading data", Toast.LENGTH_SHORT).show()
            }
    }
}