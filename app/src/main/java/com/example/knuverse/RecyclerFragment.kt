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

private val db = FirebaseFirestore.getInstance()

// ViewHolder 정의
class MyViewHolder(val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root)

// Adapter 정의
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

        // 클릭 이벤트 처리 - 카드 클릭 시 DetailActivity로 이동
        binding.itemRoot.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("DOCUMENT_ID", item.documentId) // Document ID 전달
                putExtra("SELECTED_LANGUAGE", selectedLanguage) // 선택한 언어 전달
            }
            context.startActivity(intent)
        }
    }

    // 이미지를 URL에서 불러오는 함수
    private fun loadImageFromUrl(url: String, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    }
}

// Fragment 정의
class RecyclerFragment : Fragment() {

    private var selectedLanguage: String? = null
    private var selectedCategory: String? = null

    companion object {
        fun newInstance(selectedLanguage: String, selectedCategory: String?): RecyclerFragment {
            val fragment = RecyclerFragment()
            val args = Bundle()
            args.putString("selectedLanguage", selectedLanguage)
            args.putString("selectedCategory", selectedCategory)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentRecyclerBinding.inflate(inflater, container, false)
        selectedLanguage = arguments?.getString("selectedLanguage") ?: "ko"
        selectedCategory = arguments?.getString("selectedCategory")

        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        loadData { datas ->
            binding.recyclerView.adapter = MyAdapter(datas, selectedLanguage!!)
        }

        return binding.root
    }

    // Firestore에서 데이터를 불러오는 함수
    private fun loadData(callback: (List<DocumentData>) -> Unit) {
        val collectionName = if (selectedLanguage == "en") "Partnerships_en" else "Partnerships"

        // 선택된 카테고리가 있으면 해당 카테고리의 데이터만 가져옴
        val query = if (selectedCategory != null) {
            db.collection(collectionName).whereEqualTo("category", selectedCategory)
        } else {
            db.collection(collectionName)
        }

        query.get()
            .addOnSuccessListener { result ->
                val datas = mutableListOf<DocumentData>()

                result.forEach { document ->
                    val documentId = document.id
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