package com.gachon.footprint

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.gachon.footprint.data.ModelFoot
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.footmsgrecycler.*

class FootMsgRecyclerActivity : AppCompatActivity(), RecyclerInterface {
    var footmsgInfo: ArrayList<ModelFoot> = arrayListOf()
    private lateinit var footMsgAdapter: FootMsgRecyclerAdapter
    var db: FirebaseFirestore? = null
    var lat: String? = null
    var lon: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.footmsgrecycler)
        db = FirebaseFirestore.getInstance()
        getFootMsg()

        val mapbar = findViewById<Toolbar>(R.id.map_toolbar)
        setSupportActionBar(mapbar)
        val ab: androidx.appcompat.app.ActionBar? = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
        ab?.title = "내 주변 발자취"

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
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun getFootMsg() {
        if (intent.hasExtra("LAT") && intent.hasExtra("LON")) {
            lat = intent.getStringExtra("LAT")
            lon = intent.getStringExtra("LON")
        }
        db?.collection("FootMsg")
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                footmsgInfo.clear()
                // 현재 내 위치와 FireStore에서 불러온 FootMsg 등록 위치를 비교하여 1 km이내일시 footmsginfo배열에 저장후
                // 리사이클러뷰에 뿌려줌
                // 내가 작성한 글도 보여줌
                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ModelFoot::class.java)
                    val curGps =
                        lat?.toDouble()?.let { lon?.toDouble()?.let { it1 -> LatLng(it, it1) } }
                    val tempGps =
                        item?.latitude?.let { item.longitude?.let { it1 -> LatLng(it, it1) } }
                    var distance = SphericalUtil.computeDistanceBetween(curGps, tempGps) / 1000
                    if (distance < 1) {
                        item?.distance = distance
                        footmsgInfo.add(item!!)
                    }
                }
                // 인스턴스 생성
                footMsgAdapter = FootMsgRecyclerAdapter(this)
                footMsgAdapter.submitList(footmsgInfo)
                recyclerview_footmsg.apply {
                    layoutManager = LinearLayoutManager(
                        this@FootMsgRecyclerActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter = footMsgAdapter
                }
            }
    }

    // 아이템 클릭시 포지션값 여기로 넘어옴
    override fun onItemClicked(position: Int) {
        val intent = Intent(this, RecyclerFootMsgView::class.java)
        intent.putExtra("timestamp", "${footmsgInfo[position].timestamp}")
        startActivity(intent)
    }
}

