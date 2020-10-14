package com.gachon.footprint

import android.os.Bundle
import android.view.MenuItem
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
                    var appinfo = AppInfo()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.app_info, appinfo).commit()
                }
                "1" -> {
                    var modifyinfo = UserInfoModify()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.modify_info, modifyinfo).commit()
                }
                "2" -> {
                    var changeaccount = ChangeAccount()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.change_account, changeaccount).commit()
                }
                "3" -> {
                    var setlocation = SetLocationInfo()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.set_location_info, setlocation).commit()
                }
                "4" -> {
                    var accountReport = AccountReport()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.account_report, accountReport).commit()
                }
                "5" -> {
                    var licence = Licence()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.licence, licence).commit()
                }
                "6" -> {
                    var contact = Contact()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.contact_us, contact).commit()
                }

                "7" -> {
                    var logout = LogOut()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.log_out, logout).commit()
                }
                "8" -> {
                    var withdrawaccount = WithdrawAccount()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.withdraw_account, withdrawaccount).commit()
                }
            }
        }
    }

/*    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }*/
}