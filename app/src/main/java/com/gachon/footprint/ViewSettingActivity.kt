package com.gachon.footprint

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gachon.footprint.data.CurrentUser
import com.gachon.footprint.settingfragment.*
import kotlinx.android.synthetic.main.activity_viewsetting.*

class ViewSettingActivity : AppCompatActivity() {

    var user = CurrentUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewsetting)

        //test4.text = user.uid

        if(intent.hasExtra("setting")){
            var value = intent.getStringExtra("setting")

            when(value){
                "0" -> {
                    var accountinfo = AccountInfo()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.setting_fragment, accountinfo).commit()
                }
                "1" -> {
                    var modifyinfo = UserInfoModify()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.setting_fragment, modifyinfo).commit()
                }
                "2" -> {
                    var changeaccount = ChangeAccount()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.setting_fragment, changeaccount).commit()
                }
                "3" -> {
                    var withdrawaccount = WithdrawAccount()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.setting_fragment, withdrawaccount).commit()
                }
                "4" -> {
                    var setlocation = SetLocationInfo()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.setting_fragment, setlocation).commit()
                }
                "5" -> {
                    var accountreport = AccountReport()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.setting_fragment, accountreport).commit()
                }
                "6" -> {
                    var appinfo = AppInfo()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.setting_fragment, appinfo).commit()
                }
                "7" -> {
                    var licence = Licence()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.setting_fragment, licence).commit()
                }
                "8" -> {
                    var contact = Contact()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.setting_fragment, contact).commit()
                }
            }
        }
    }
}