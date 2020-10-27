package com.gachon.footprint.reviewRecyclerView

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gachon.footprint.App
import com.gachon.footprint.data.ModelReview
import kotlinx.android.synthetic.main.recyclerview_item.view.review_nickname
import kotlinx.android.synthetic.main.recyclerview_item.view.review_profile
import kotlinx.android.synthetic.main.review_item.view.*

// 커스텀 뷰홀더
class ReviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val profile = itemView.review_profile
    private val nickname = itemView.review_nickname
    private val review_msg = itemView.review_msg

    fun bind(reviewList: ModelReview) {
        nickname.text = reviewList.nickname
        review_msg.text = reviewList.reviewtxt
        if (reviewList.imageUrl != null) {
            Glide.with(App.instance).load(reviewList.imageUrl).override(300, 225).into(profile)
        }

    }
}