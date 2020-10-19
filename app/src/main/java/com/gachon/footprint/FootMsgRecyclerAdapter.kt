package com.gachon.footprint

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gachon.footprint.data.ModelFoot

class FootMsgRecyclerAdapter : RecyclerView.Adapter<FootMsgHolder>(){

    private var footmsgInfo = ArrayList<ModelFoot>()

    // 뷰 홀더가 생성 되었을때
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FootMsgHolder {
        // 연결할 레이아웃 설정
        return FootMsgHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false))
    }

    override fun onBindViewHolder(holder: FootMsgHolder, position: Int) {
        holder.bind(this.footmsgInfo[position])
    }
    // 목록의 아이템 수
    override fun getItemCount(): Int {
        return footmsgInfo.size
    }

    // 외부에서 데이터 넘기기

    fun submitList(modelFoot: ArrayList<ModelFoot>){
        this.footmsgInfo = modelFoot
    }
}