package com.gachon.footprint.diaryRecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gachon.footprint.R
import com.gachon.footprint.RecyclerInterface
import com.gachon.footprint.data.ModelFoot
import java.util.*

class DiaryRecyclerAdapter(recyclerInterface: RecyclerInterface) :
    RecyclerView.Adapter<DiaryHolder>() {

    private var diaries = ArrayList<ModelFoot>()

    private var recyclerInterface: RecyclerInterface? = null

    init {
        this.recyclerInterface = recyclerInterface
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryHolder {
        return DiaryHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.diary_detail, parent, false),
            this.recyclerInterface!!
        )
    }

    override fun onBindViewHolder(holder: DiaryHolder, position: Int) {
        holder.bind(this.diaries[position])
    }

    override fun getItemCount(): Int {
        return diaries.size
    }

    fun submitList(modelList: ArrayList<ModelFoot>) {
        this.diaries = modelList
    }

}