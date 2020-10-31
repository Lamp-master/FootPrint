package com.gachon.footprint.footMsgRecyclerView

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gachon.footprint.App
import com.gachon.footprint.R
import com.gachon.footprint.RecyclerInterface
import com.gachon.footprint.data.ModelFoot
import kotlinx.android.synthetic.main.recyclerview_item.view.*

class FootMsgHolder(
    itemView: View,
    recyclerInterface: RecyclerInterface
) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val title_name = itemView.review_nickname
    private val footMsg_Img = itemView.review_profile
    private val distance = itemView.distance
    private var recyclerInterface: RecyclerInterface? = null

    // 생성자
    init {
        itemView.setOnClickListener(this)
        this.recyclerInterface = recyclerInterface
    }

    fun bind(footmsgInfo: ModelFoot) {
        title_name.text = footmsgInfo.title
        var between = String.format("%.2f", footmsgInfo.distance)
        distance.text = between + "km"
        if (footmsgInfo.imageUrl != null) {
            Glide.with(App.instance).load(footmsgInfo.imageUrl).placeholder(R.mipmap.ic_launcher)
                .into(footMsg_Img)
        }
    }

    override fun onClick(v: View?) {
        this.recyclerInterface?.onItemClicked(adapterPosition)
    }


}