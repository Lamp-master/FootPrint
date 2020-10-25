package com.gachon.footprint

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.gachon.footprint.data.ModelFoot
import com.google.android.gms.location.LocationServices
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
    var sydney: LatLng? = null

    class LatLngData(
        val latLng: LatLng,
        val title: String
    ) {}

    var latlngdata: LatLngData? = null
    var distance: Double? = null
    var footmsgInfo: ModelFoot? = ModelFoot()

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
        getLastLocationNewMethod()
        getFootMsgGps()

        mMap!!.setOnMarkerClickListener(this)
        mMap!!.setOnMapClickListener(this)
    }

    fun getLastLocationNewMethod() {
        val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.apply {
                    val sydney = LatLng(this.latitude, this.longitude)
                    lat = latitude
                    lon = longitude
                    getCurrentLoc()
                    mMap?.addMarker(
                        MarkerOptions().position(sydney).title(currentLocation)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_black))
                    )

                    mMap?.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                    mMap?.animateCamera(CameraUpdateFactory.zoomTo(16f))

                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

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

    private fun setContent() {
        card_view.visibility = View.VISIBLE
        map_title.setText(footmsgInfo?.title)
        map_footmsg.setText(footmsgInfo?.msgText)
        Glide.with(this).load(footmsgInfo?.imageUrl).into(map_footImg)


    }

    override fun onMapClick(p0: LatLng?) {
        card_view.visibility = View.INVISIBLE
    }
}