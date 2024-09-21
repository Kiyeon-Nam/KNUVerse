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

class MyAdapter(val datas: MutableList<String>) : RecyclerView.Adapter<MyViewHolder>() {


    // 항목 개수 판단하기 위해 자동 호출
    override fun getItemCount(): Int = datas.size

    // 항목 뷰를 가지는 뷰 홀더를 준비하기 위해 자동 호출
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    // 각 항목을 구성하기 위해 호출
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding
        // binding.cardHead.text
        Log.d("test", "item root click : $position")
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
                    binding.cardHead.text = title
                    binding.cardText.text = content
                    binding.cardDate.text = "${startDate?.let{dateFormat.format(it.toDate())}} ~ ${endDate?.let{dateFormat.format(it.toDate())}}"

                    if (isPartnership == true) {
                        binding.cardStatus.text = "제휴"
                    } else {
                        binding.cardStatus.text = "홍보"
                    }

                    // 이미지가 있으면 첫 번째 이미지 로드
                    if (imageUrls != null && imageUrls.isNotEmpty()) {
                        loadImageFromUrl(imageUrls[0], binding.cardThumbnail)
                    } else {
                        Toast.makeText(holder.itemView.context, "No images found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firebase", "Error getting documents.", exception)
                Toast.makeText(holder.itemView.context, "Error loading data", Toast.LENGTH_SHORT).show()
            }
    }

    // Glide를 사용하여 이미지 로드
    private fun loadImageFromUrl(url: String, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    }

}

class RecyclerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRecyclerBinding.inflate(inflater, container, false)

        // RecyclerView 설정
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)

        // Firestore에서 데이터 가져오기
        db.collection("Partnerships")
            .get()
            .addOnSuccessListener { result ->
                val datas = mutableListOf<String>() // 데이터 저장할 리스트

                for (document in result) {
                    // 제목을 리스트에 추가 (필요한 데이터를 적절히 추가하세요)
                    val title = document.getString("title") ?: "제목 없음"
                    datas.add(title)
                }

                // RecyclerView에 어댑터 설정
                binding.recyclerView.adapter = MyAdapter(datas)
            }
            .addOnFailureListener { exception ->
                Log.w("Firebase", "Error getting documents.", exception)
                Toast.makeText(requireContext(), "Error loading data", Toast.LENGTH_SHORT).show()
            }

        return binding.root
    }
}