package com.example.knuverse

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.knuverse.databinding.FragmentRecyclerBinding

class MyViewHolder(val binding: FragmentRecyclerBinding) : RecyclerView.ViewHolder(binding.root)

class MyAdapter(val datas: MutableList<String>) : RecyclerView.Adapter<MyViewHolder>() {

    // 항목 개수 판단하기 위해 자동 호출
    override fun getItemCount(): Int = datas.size

    // 항목 뷰를 가지는 뷰 홀더를 준비하기 위해 자동 호출
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = FragmentRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    // 각 항목을 구성하기 위해 호출
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = datas[position]

        // 데이터 바인딩. 아래 주석은 예시임. 이 프로젝트에는 저런 항목이 없음
        // TODO("카드 데이터 바인딩")
        // holder.binding.cardTitle.text = item.title
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