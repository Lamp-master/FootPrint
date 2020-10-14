package com.gachon.footprint

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.gachon.footprint.settingfragment.*

class ViewSettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_setting)

        if (intent.hasExtra("setting")) {
            var value = intent.getStringExtra("setting")
            when (value) {
                "0" -> {
                    var appinfo = AppInfo()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, appinfo).commit()
                }
                "1" -> {
                    var modifyinfo = UserInfoModify()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, modifyinfo).commit()
                }
                "2" -> {
                    var changeaccount = ChangeAccount()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, changeaccount).commit()
                }
                "3" -> {
                    var setlocation = SetLocationInfo()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, setlocation).commit()
                }
                "4" -> {
                    var accountreport = AccountReport()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, accountreport).commit()
                }
                "5" -> {
                    var licence = Licence()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, licence).commit()
                }
                "6" -> {
                    var contact = Contact()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, contact).commit()
                }
                "7" -> {
                    var logOut = LogOut()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, logOut).commit()
                }
                "8" -> {
                    var withdrawaccount = WithdrawAccount()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, withdrawaccount).commit()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}