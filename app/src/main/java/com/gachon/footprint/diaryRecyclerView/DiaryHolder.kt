package com.gachon.footprint.diaryRecyclerView

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gachon.footprint.App
import com.gachon.footprint.data.ModelFoot
import com.gachon.footprint.data.ModelReview
import kotlinx.android.synthetic.main.diary_detail.view.*
import kotlinx.android.synthetic.main.recyclerview_item.view.*
import kotlinx.android.synthetic.main.review_item.view.*

class DiaryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val title = itemView.diary_title
    private val contextImg = itemView.diary_img
    private val context = itemView.diary_context
/*    private val location = itemView.diary_location*/

    fun bind(diaries: ModelFoot) {
        title.text = diaries.title
        if (diaries.imageUrl != null) {
            Glide.with(App.instance).load(diaries.imageUrl).override(300, 225).into(contextImg)
        }
        context.text = diaries.msgText
 /*       location.text = diaries.*/
    }
}