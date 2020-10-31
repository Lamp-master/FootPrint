package com.gachon.footprint

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.gachon.footprint.data.ModelFoot
import com.gachon.footprint.diaryRecyclerView.DiaryRecyclerAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_diary.*
import kotlinx.android.synthetic.main.diary_detail.*
import timber.log.Timber

class DiaryActivity : AppCompatActivity(), RecyclerInterface {

    var diaryInfo: ArrayList<ModelFoot> = arrayListOf()
    var db: FirebaseFirestore? = null
    var user = FirebaseAuth.getInstance().currentUser
    private lateinit var diaryRecyclerAdapter: DiaryRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        val mapbar = findViewById<Toolbar>(R.id.Diary_toolbar)
        setSupportActionBar(mapbar)
        val ab: androidx.appcompat.app.ActionBar? = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
        ab?.title = "다이어리"


        db = FirebaseFirestore.getInstance()
        Timber.d("Test checked user uid ${user?.uid}")
        db?.collection("User")?.document(user?.uid.toString())?.collection("Diary")?.get()?.addOnSuccessListener { querySnapshot ->
            for (snapshot in querySnapshot!!.documents) {
                val item = snapshot.toObject(ModelFoot::class.java)
                if (item?.uid == user?.uid) {
                    item?.footMsgId = snapshot.id
                    Timber.d("Test checked document uid ${snapshot.id}")
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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar, menu)
        //맵 툴바를 가져옴
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            //뒤로가기 버튼
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onItemClicked(position: Int) {
        Timber.d("Test click list in diray")
        val intent = Intent(this, RecyclerDiaryViewActivity::class.java)
        intent.putExtra("FootMsgId", "${diaryInfo[position].footMsgId}")
        startActivity(intent)
    }


}