package com.gachon.footprint

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gachon.footprint.data.ModelFoot
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.footmsgrecycler.*
import timber.log.Timber

class FootMsgRecyclerActivity : AppCompatActivity() {
    var footmsgInfo: ArrayList<ModelFoot> = arrayListOf()
    private lateinit var footMsgAdapter: FootMsgRecyclerAdapter
    var db: FirebaseFirestore? = null
    var lat: String? = null
    var lon: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.footmsgrecycler)
        Timber.plant(Timber.DebugTree())
        db = FirebaseFirestore.getInstance()
        getFootMsg()

    }

    fun getFootMsg() {
        Timber.d("test")
        if (intent.hasExtra("LAT") && intent.hasExtra("LON")) {
            lat = intent.getStringExtra("LAT")
            lon = intent.getStringExtra("LON")
            Timber.d("TestGps $lat $lon")}
        Timber.d("TestGps $lat $lon")
        db?.collection("FootMsg")
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                footmsgInfo.clear()
                // 현재 내 위치와 FireStore에서 불러온 FootMsg 등록 위치를 비교하여 1 km이내일시 footmsginfo배열에 저장후
                // 리사이클러뷰에 뿌려줌줌
                // 내가 작성한 글도 보여줌
                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ModelFoot::class.java)
                    val curGps = lat?.toDouble()?.let { lon?.toDouble()?.let { it1 -> LatLng(it, it1) } }
                    val tempGps = item?.latitude?.let { item.longitude?.let { it1 -> LatLng(it, it1) } }
                    var distance = SphericalUtil.computeDistanceBetween(curGps, tempGps) / 1000
                    if (distance < 1) {
                        item?.distance = distance
                        footmsgInfo.add(item!!)
                    }
                }
                // 인스턴스 생성
                footMsgAdapter = FootMsgRecyclerAdapter()
                footMsgAdapter.submitList(footmsgInfo)
                recyclerview_footmsg.apply {
                    layoutManager = LinearLayoutManager(this@FootMsgRecyclerActivity, LinearLayoutManager.VERTICAL,false)
                    adapter = footMsgAdapter
                }
            }
    }
}

