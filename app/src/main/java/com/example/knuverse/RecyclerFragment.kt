package com.example.knuverse

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.knuverse.databinding.FragmentRecyclerBinding
import com.example.knuverse.databinding.ItemCardBinding

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
    }

}

class RecyclerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRecyclerBinding.inflate(inflater, container, false)

        // 데이터 준비
        // TODO("여기에 가상 데이터 넣어볼 수 있음")
        val datas = mutableListOf<String>()
        for (i in 1..9) {
            datas.add("Item $i")
        }

        // RecyclerView 설정
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = MyAdapter(datas)

        return binding.root
    }
}

// TODO("필요한 데이터 정의")
// 썸네일이미지, 제목, 제휴/홍보, 시작날짜, 종료날짜, 본문
data class CardData(val thumbnailResId: Int, val title: String, val status: String, val startDate: Int, val endDate: Int, val description: String)