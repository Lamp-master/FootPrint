package com.gachon.footprint

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gachon.footprint.navigation.CashFragment
import com.gachon.footprint.navigation.HomeFragment
import com.gachon.footprint.navigation.SettingFragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    companion object {
        var sydney: LatLng? = null
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.action_home -> {
                var homeFragment = HomeFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, homeFragment).commit()
                return true
            }

            R.id.action_cash -> {
                var cashFragment = CashFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, cashFragment)
                    .commit()
                return true
            }
            R.id.action_setting -> {
                var settingFragment = SettingFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, settingFragment).commit()
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        bottom_navigation.selectedItemId=R.id.action_home

        if (ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, permissions,0)
        }
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions, 0)

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
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            mFusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.apply {
                        sydney = LatLng(this.latitude, this.longitude)
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }
}



