package com.gachon.footprint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gachon.footprint.data.ModelFoot
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.activity_recycler_view__view_foot_msg.*
import kotlinx.android.synthetic.main.recyclerview_item.view.*
import timber.log.Timber

class RecyclerView_ViewFootMsg : AppCompatActivity() {

    var db : FirebaseFirestore? = null
    var lat: String? = null
    var lon: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view__view_foot_msg)

        db = FirebaseFirestore.getInstance()
        recyclerview_footmsg.adapter = RecyclerViewAdapter()
        recyclerview_footmsg.layoutManager = LinearLayoutManager(this)
        // Map activity로 부터 GPS정보 가져옴

        if(intent.hasExtra("LAT") && intent.hasExtra("LON")) {
            lat = intent.getStringExtra("LAT")
            lon = intent.getStringExtra("LON")
            Timber.d("TestGps $lat $lon")
        }
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        // ModelFoot 클래서 ArrayList 생성
        var footmsgInfo : ArrayList<ModelFoot> = arrayListOf()

            init { // FootMsg의 문서를 불러온 뒤 ModelFoot으로 변환해 ArrayList에 담음
                db?.collection("FootMsg")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    footmsgInfo.clear()
                    // 현재 내 위치와 FireStore에서 불러온 FootMsg 등록 위치를 비교하여 1 km이내일시 footmsginfo배열에 저장후
                    // 리사이클러뷰에 뿌려줌줌
                    // 내가 작성한 글도 보여줌
                   for(snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ModelFoot::class.java)
                        val curGps = lat?.toDouble()?.let { lon?.toDouble()?.let { it1 -> LatLng(it, it1) } }
                        val tempGps = item?.latitude?.let { item.longitude?.let { it1 -> LatLng(it, it1) } }
                        var distance = SphericalUtil.computeDistanceBetween(curGps, tempGps) / 1000
                        if(distance < 1){
                            item?.distance = distance
                            footmsgInfo.add(item!!)
                        }
                    }
                    notifyDataSetChanged()
                }
            }

        // xml 파일을 inflate 하여 ViewHolder 생성
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        }

        //onCreateViewHolder 에서 만든 view와 실제 데이터를 연결
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as CustomViewHolder).itemView
            viewHolder.title.text = footmsgInfo[position].title
            viewHolder.distance.text = footmsgInfo[position].distance.toString() + "km"
        }
        // 리사이클러뷰의 아이템 총 개수 반환
        override fun getItemCount(): Int {
            return footmsgInfo.size
        }
    }
}