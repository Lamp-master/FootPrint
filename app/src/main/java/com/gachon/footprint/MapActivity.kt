package com.gachon.footprint

import android.Manifest
import com.google.maps.android.SphericalUtil
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import timber.log.Timber
import java.io.IOException
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    var lat: Double? = 0.0
    var lon: Double? = 0.0
    var currentLocation: String = ""

    class LatLngData(
        val id: Long,
        val latLng: LatLng
    ) {}

    val latlngdata = arrayListOf<LatLngData>()
    var distance: Double? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)


        latlngdata.add(LatLngData(1, LatLng(37.5082, 126.8433)))
        latlngdata.add(LatLngData(2, LatLng(37.5109, 126.8411)))
        latlngdata.add(LatLngData(3, LatLng(37.5166, 126.8414)))
        latlngdata.add(LatLngData(4, LatLng(37.5127, 126.8526)))
        latlngdata.add(LatLngData(5, LatLng(37.5119, 126.8583)))
        latlngdata.add(LatLngData(6, LatLng(37.5202, 126.8473)))
        latlngdata.add(LatLngData(7, LatLng(37.5164, 126.8543)))
        
        val mapbar = findViewById<Toolbar>(R.id.map_toolbar)
        setSupportActionBar(mapbar)
        val ab: androidx.appcompat.app.ActionBar? = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
        ab?.title = "내 주변 발자취"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_toolbar, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getLastLocationNewMethod()
    }

    fun getLastLocationNewMethod() {
        val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mFusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.apply {
                    val sydney = LatLng(this.latitude, this.longitude)
                    lat = latitude
                    lon = longitude
                    Timber.d("Test $lat $lon")
                    getCurrentLoc()
                    distanceGps()
                    mMap?.addMarker(MarkerOptions().position(sydney).title(currentLocation))
                    mMap?.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                    mMap?.animateCamera(CameraUpdateFactory.zoomTo(16f))

                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
    // 두 좌표간 거리를 계산하여 1km 이내일시 마커 표시
    fun distanceGps() {
        val cur = LatLng(lat!!, lon!!)
        for (i in latlngdata.indices) {
            distance = SphericalUtil.computeDistanceBetween(cur, latlngdata[i].latLng) / 1000
            if (distance!! < 1) {
                Timber.d("Test $distance")
                addMarker(latlngdata[i])
                Toast.makeText(this, "km" + distance!!, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMarker(latlngdata: LatLngData): Marker {
        return mMap?.addMarker(MarkerOptions().position(latlngdata.latLng))!!

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

}


