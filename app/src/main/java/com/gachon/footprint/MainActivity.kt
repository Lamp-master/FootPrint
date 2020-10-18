package com.gachon.footprint

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    var sydney: LatLng? = null
    var lat: Double? = 0.0
    var lon: Double? = 0.0
    private val db = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())
        auth = FirebaseAuth.getInstance()
        getLastLocationNewMethod()
        Timber.d("Test ${sydney?.latitude}")

        //권한 설정
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions, 0) }

        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA )
        ActivityCompat.requestPermissions(this, permissions, 0)

        //액티비티 전환 버튼 추가

        add_footprint.setOnClickListener {
            val intent = Intent(this, FootMsgActivity::class.java)
            intent.putExtra("LON", sydney?.longitude)
            intent.putExtra("LAT", sydney?.latitude)
            Timber.d("Test ${sydney?.latitude}")
            Timber.d("Test ${sydney?.longitude}")
            startActivity(intent)
        }
        // 발자취 찾기
        find_footprint.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
        //근처 발자취 보기 (Google map)
        near_footprint.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
        // 다이어리 보기
        my_diary.setOnClickListener {
            val intent = Intent(this, DiaryActivity::class.java)
            startActivity(intent)
        }
        //상품 구매하기
        /*buy_goods.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }*/
        //설정 보기
        setting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }

    // 실행시 첫 메인화면에서 gps값을 가져오는 건지 모르겠음. 확인해봐야함
    // firestore 에는 gps값 등록이 됨.
    // 등록안됨;;
    fun getLastLocationNewMethod() {
        val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            mFusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.apply {
                        sydney = LatLng(this.latitude, this.longitude)
                        lat = latitude
                        lon = longitude
                        Timber.d("Test $lat")
                        Timber.d("Test $lon")
                        db.collection("User").document(auth?.uid.toString()).set(sydney!!, SetOptions.merge())
                            .addOnSuccessListener { void: Void? ->
                            }
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
    }
}





/*
  db.collection("User").document(auth?.uid.toString()).set(userInfo)
  .addOnSuccessListener { void: Void? ->

*/
// firestore 로부터 유저 정보 받아옴. 이 Activity에선 필요없음
/*
private fun getUserInfo(){
    var cuser = FirebaseAuth.getInstance().currentUser
    if(cuser!=null) {
        user.uid = cuser.uid
        db.collection("User").document(user.uid!!).get().addOnSuccessListener { documentSnapshot ->
            var map: Map<String, Any> = documentSnapshot.data as Map<String, Any>
            user.nickname = map["nickname"].toString()
            user.email = map["userEmail"].toString()
            user.password = map["password"].toString()
            user.Img = map["userImg"].toString()
        }
    }
}
*/
