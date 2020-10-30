package com.gachon.footprint

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gachon.footprint.data.ModelFoot
import com.gachon.footprint.diaryRecyclerView.DiaryRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_diary.*
import timber.log.Timber

class DiaryActivity : AppCompatActivity(), RecyclerInterface {

    var diaryInfo: ArrayList<ModelFoot> = arrayListOf()
    var db: FirebaseFirestore? = null
    var user = FirebaseAuth.getInstance().currentUser
    private lateinit var diaryRecyclerAdapter: DiaryRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        db = FirebaseFirestore.getInstance()

        db?.collection("FootMsg")?.get()?.addOnSuccessListener { querySnapshot ->
            for (snapshot in querySnapshot!!.documents) {
                val item = snapshot.toObject(ModelFoot::class.java)
                if (item?.uid == user?.uid) {
                    item?.footMsgId = snapshot.id
                    diaryInfo.add(item!!)
                    diaryRecyclerAdapter = DiaryRecyclerAdapter(this)
                    diaryRecyclerAdapter.submitList(this.diaryInfo)
                    recycler_diary.apply {
                        layoutManager =
                            LinearLayoutManager(
                                this@DiaryActivity,
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                        adapter = diaryRecyclerAdapter
                    }

                }
            }
        }
    }

    override fun onItemClicked(position: Int) {
        Timber.d("Test checked diaryactivity")
        val intent = Intent(this, RecyclerDiaryViewActivity::class.java)
        intent.putExtra("FootMsgId", "${diaryInfo[position].footMsgId}")
        startActivity(intent)
    }


}