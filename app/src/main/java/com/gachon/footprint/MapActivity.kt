package com.gachon.footprint

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    var lat: Double? = 0.0
    var lon: Double? = 0.0

    var currentLocation: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
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
                    getCurrentLoc()
                    mMap?.addMarker(MarkerOptions().position(sydney).title(currentLocation))
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

}


