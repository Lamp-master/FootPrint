package com.gachon.footprint

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.gachon.footprint.navigation.CashFragment
import com.gachon.footprint.navigation.DetailViewFragment
import com.gachon.footprint.navigation.SettingFragment
import com.gachon.footprint.navigation.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId) {
            R.id.action_home ->{
                var detailViewFragment = DetailViewFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, detailViewFragment).commit()
                return true
            }
            R.id.action_user ->{
                var userFragment = UserFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, userFragment).commit()
                return true
            }
            R.id.action_cash ->{
                var cashFragment = CashFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, cashFragment).commit()
                return true
            }
            R.id.action_setting ->{
                var settingFragment = SettingFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, settingFragment).commit()
                return true
            }
        }
        return false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottom_navigation.setOnNavigationItemSelectedListener(this)
    }

}

