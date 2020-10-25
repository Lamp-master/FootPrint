package com.gachon.footprint.reviewRecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gachon.footprint.R
import com.gachon.footprint.data.ModelReview
import java.util.*

class ReviewRecyclerAdapter : RecyclerView.Adapter<ReviewHolder>() {

    private var reviewList = ArrayList<ModelReview>()

    // 뷰홀더가 생성 되었을때
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewHolder {
        // 연결할 레이아웃 설정

        return ReviewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
        )
    }

    // 뷰의 뷰홀더가 묶였을때 , 재활용이 되었을때
    override fun onBindViewHolder(holder: ReviewHolder, position: Int) {
        holder.bind(this.reviewList[position])
    }

    // 목록의 아이템 갯수
    override fun getItemCount(): Int {
        return reviewList.size
    }

    fun submitList(modelList: ArrayList<ModelReview>) {
        this.reviewList = modelList
    }

}