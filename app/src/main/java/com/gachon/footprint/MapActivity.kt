package com.gachon.footprint

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.gachon.footprint.data.ModelFoot
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.s_modify_info.*
import timber.log.Timber
import java.io.IOException
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapClickListener {
    private var mMap: GoogleMap? = null
    private var auth: FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance()
    var lat: Double? = 0.0
    var lon: Double? = 0.0
    var currentLocation: String = ""
    var locationRequset: LocationRequest? = null
    var fusedLocationClient: FusedLocationProviderClient? = null
    var locationCallback: LocationCallback? = null

    var sydney: LatLng? = null

    class LatLngData(
        val latLng: LatLng,
        val title: String
    ) {}

    var latlngdata: LatLngData? = null
    var distance: Double? = null
    var footmsgInfo: ModelFoot? = ModelFoot()

    // 화면이 보이는 시점에 위치정보를 요청, 위치서비스에 데이터를 요청할 객체를 생성하고 정확도와 인터벌 시간을 정해줌
    override fun onResume() {
        super.onResume()
        locationRequset = LocationRequest.create()
        locationRequset?.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 60 * 1000
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    for ((i, location) in it.locations.withIndex()) {
                        Log.d("Test", "")
                    }
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient?.requestLocationUpdates(
            locationRequset,
            locationCallback,
            Looper.myLooper()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.plant(Timber.DebugTree())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        auth = FirebaseAuth.getInstance()
        val mapbar = findViewById<Toolbar>(R.id.map_toolbar)
        setSupportActionBar(mapbar)
        val ab: androidx.appcompat.app.ActionBar? = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
        ab?.title = "내 주변 발자취"

        if (intent.hasExtra("LAT") && intent.hasExtra("LON")) {
            lat = intent.getStringExtra("LAT")?.toDouble()
            lon = intent.getStringExtra("LON")?.toDouble()
        }

        getFootMsgGps()

        btn_map_footprint.setOnClickListener {
            val intent = Intent(this, FootMsgRecyclerActivity::class.java)
            intent.putExtra("LAT", "$lat")
            intent.putExtra("LON", "$lon")
            startActivity(intent)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_toolbar, menu)
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
            R.id.my_location -> {
                mMap?.moveCamera(CameraUpdateFactory.newLatLng(lat?.let {
                    lon?.let { it1 ->
                        LatLng(it, it1)
                    }
                }))

                mMap?.animateCamera(CameraUpdateFactory.zoomTo(16f))
                //툴바의 아이콘이 눌리면 중괄호 안을 하겠다..
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        initLocation()
        getFootMsgGps()
        mMap!!.setOnMarkerClickListener(this)
        mMap!!.setOnMapClickListener(this)
    }

    //현재 위치 지리정보 받아옴
    fun getCurrentLoc() {
        val mGeoCoder = Geocoder(applicationContext, Locale.KOREAN)
        var mResultList: List<Address>? = null
        try {
            mResultList = mGeoCoder.getFromLocation(lat!!, lon!!, 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (mResultList != null) {
            currentLocation = mResultList[0].getAddressLine(0)
            currentLocation = currentLocation.substring(11)
        }
    }

    //주변 메시지 위치 받아옴
    private fun getFootMsgGps() {
        db.collection("FootMsg").get().addOnSuccessListener { documents ->
            for (document in documents) {
                var map: Map<String, Any> = document.data
                var tempLat = map["latitude"].toString()
                var tempLon = map["longitude"].toString()
                distanceGps(tempLat, tempLon)
            }
        }
    }

    // firestore에서 위도/경도 받아와 내 현재위치와 거리비교
    fun distanceGps(tempLat: String, tempLon: String) {
        val cur = this.lat?.let { this.lon?.let { it1 -> LatLng(it, it1) } }
        val tempGps = LatLng(tempLat.toDouble(), tempLon.toDouble())
        distance = SphericalUtil.computeDistanceBetween(cur, tempGps) / 1000
        if (distance!! < 1) {
            latlngdata = LatLngData(LatLng(tempLat.toDouble(), tempLon.toDouble()), "")
            addMarker(latlngdata!!)
        }
    }

    // 두 좌표간 거리를 계산하여 1km 이내일시 마커 표시
    private fun addMarker(latlngdata: LatLngData): Marker {
        return mMap?.addMarker(MarkerOptions().position(latlngdata.latLng))!!
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        db.collection("FootMsg").get().addOnSuccessListener { documents ->
            for (document in documents) {
                var map: Map<String, Any> = document.data
                if (map["latitude"].toString() == marker?.position?.latitude.toString() && map["longitude"].toString() == marker?.position?.longitude.toString()) {
                    footmsgInfo = document.toObject(ModelFoot::class.java)
                    setContent()
                }
            }
        }
        return true
    }

    //마커 클릭시 정보 보여줌
    private fun setContent() {
        card_view.visibility = View.VISIBLE
        map_title.setText(footmsgInfo?.title)
        map_footmsg.setText(footmsgInfo?.msgText)
        Glide.with(this).load(footmsgInfo?.imageUrl).into(map_footImg)


    }

    // 맵 클릭하면 마커 상세보기 사라짐
    override fun onMapClick(p0: LatLng?) {
        card_view.visibility = View.INVISIBLE
    }


    //현재 위치를 가져 오는 함수 , 권한이 있는지 런타임에 확인하고 권한이 있으면 위치 서비스에 연결시킨후 lastlocation 을 통해 현재위치 가져옴
    private fun initLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
            if (location == null) {
            } else {
                var sydney = LatLng(location.latitude, location.longitude)
                lat = location.latitude
                lon = location.longitude
                getCurrentLoc()

                mMap?.addMarker(
                    MarkerOptions().position(sydney).title(currentLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_black))
                )

                mMap?.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                mMap?.animateCamera(CameraUpdateFactory.zoomTo(16f))

                db.collection("User").document(auth?.uid.toString())
                    .set(sydney, SetOptions.merge())

            }
        }
            ?.addOnFailureListener { e ->
                e.printStackTrace()
            }

    }

    // 화면이 사라지는 시점에 콜백 리스너를 해제
    override fun onPause() {
        super.onPause()
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }
}