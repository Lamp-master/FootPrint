package com.gachon.footprint

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gachon.footprint.data.ModelFoot
<<<<<<< HEAD
import com.gachon.footprint.diaryRecyclerView.DiaryRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_diary.*
import timber.log.Timber

class DiaryActivity : AppCompatActivity(), RecyclerInterface {
=======
import com.gachon.footprint.data.ModelReview
import com.gachon.footprint.diaryRecyclerView.DiaryRecyclerAdapter
import com.gachon.footprint.reviewRecyclerView.ReviewRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_diary.*
import kotlinx.android.synthetic.main.activity_recycler_foot_msg_view.*
import kotlinx.android.synthetic.main.diary_detail.*
import kotlinx.android.synthetic.main.footprint_dialog.*

class DiaryActivity : AppCompatActivity() {
>>>>>>> 8c26e49d6e5b8512a5e537fed085aff5b490749b

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
<<<<<<< HEAD
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

=======
/*                    item?.title
                    diaries.add(snapshot)*/
                    diaryInfo.add(item!!)

                    diaryRecyclerAdapter = DiaryRecyclerAdapter()
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
>>>>>>> 8c26e49d6e5b8512a5e537fed085aff5b490749b
                }
            }
        }
    }

    override fun onItemClicked(position: Int) {
        Timber.d("Test checked diaryactivity")
        val intent = Intent(this, RecyclerDiaryViewActivity::class.java)
        intent.putExtra("timestamp", "${diaryInfo[position].timestamp}")
        startActivity(intent)
    }


}