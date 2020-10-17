package com.gachon.footprint

import android.view.View
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gachon.footprint.data.ModelFoot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.recyclerview_item.view.*
import kotlinx.android.synthetic.main.s_modify_info.*
import timber.log.Timber

class FootMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title_name = itemView.title_name
    private val footMsg_Img = itemView.footImg
    private val distance = itemView.distance
    init{

    }
    fun bind(footmsgInfo: ModelFoot){
        title_name.text = footmsgInfo.title
        distance.text = footmsgInfo.distance.toString()
        Glide.with(App.instance).load(footmsgInfo.imageUrl).placeholder(R.mipmap.ic_launcher).into(footMsg_Img)
        Timber.d("testuri ${footmsgInfo.imageUrl}")
    }
}