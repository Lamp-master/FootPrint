package com.gachon.footprint

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gachon.footprint.data.CurrentUser
import com.gachon.footprint.settingfragment.*
import kotlinx.android.synthetic.main.activity_setting.*

import timber.log.Timber

class SettingActivity : AppCompatActivity() {

    var user = CurrentUser()
    val ft = supportFragmentManager.beginTransaction()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        Timber.plant(Timber.DebugTree())

        account_info.setOnClickListener {
            val intent = Intent(this, ViewSettingActivity::class.java)
            intent.putExtra("setting", "0")
            startActivity(intent)
        }
        modify_info.setOnClickListener {
            val intent = Intent(this, ViewSettingActivity::class.java)
            intent.putExtra("setting", "1")
            startActivity(intent)
        }
        change_account.setOnClickListener {
            val intent = Intent(this, ViewSettingActivity::class.java)
            intent.putExtra("setting", "2")
            startActivity(intent)
        }
        withdraw_account.setOnClickListener {
            val intent = Intent(this, ViewSettingActivity::class.java)
            intent.putExtra("setting", "3")
            startActivity(intent)
        }
        set_location_info.setOnClickListener {
            val intent = Intent(this, ViewSettingActivity::class.java)
            intent.putExtra("setting", "4")
            startActivity(intent)
        }
        account_report.setOnClickListener {
            val intent = Intent(this, ViewSettingActivity::class.java)
            intent.putExtra("setting", "5")
            startActivity(intent)
        }
        app_info.setOnClickListener {
            val intent = Intent(this, ViewSettingActivity::class.java)
            intent.putExtra("setting", "6")
            startActivity(intent)

        }
        licence.setOnClickListener {
            val intent = Intent(this, ViewSettingActivity::class.java)
            intent.putExtra("setting", "7")
            startActivity(intent)
        }
        contact_us.setOnClickListener {
            val intent = Intent(this, ViewSettingActivity::class.java)
            intent.putExtra("setting", "8")
            startActivity(intent)
        }
    }
}



/*이걸 여기로 꺼내도될까? 하튼이걸로 뒤로가기가되긴되는데
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }*/
/*
if(intent.hasExtra("setting")){
    var value = intent.getStringExtra("setting")
    Timber.d("Test $value")
    //아니이게 왜안이어질까요?
    when(value){
        "0" -> {
            var accountinfo = AccountInfo()
            supportFragmentManager.beginTransaction()
                .replace(R.id.account_info, accountinfo).commit()
        }
        "1" -> {
            var modifyinfo = UserInfoModify()
            Timber.d("Test $value")
            supportFragmentManager.beginTransaction()
                .replace(R.id.s_modify_info, modifyinfo).commit()
        }
        "2" -> {
            var changeaccount = ChangeAccount()
            supportFragmentManager.beginTransaction()
                .replace(R.id.change_account, changeaccount).commit()
        }
        "3" -> {
            var withdrawaccount = WithdrawAccount()
            supportFragmentManager.beginTransaction()
                .replace(R.id.withdraw_account, withdrawaccount).commit()
        }
        "4" -> {
            var setlocation = SetLocationInfo()
            supportFragmentManager.beginTransaction()
                .replace(R.id.set_location_info, setlocation).commit()
        }
        "5" -> {
            var accountreport = AccountReport()
            supportFragmentManager.beginTransaction()
                .replace(R.id.account_info, accountreport).commit()
        }
        "6" -> {
            var appinfo = AppInfo()
            supportFragmentManager.beginTransaction()
                .replace(R.id.app_info, appinfo).commit()
        }
        "7" -> {
            var licence = Licence()
            supportFragmentManager.beginTransaction()
                .replace(R.id.licence, licence).commit()
        }
        "8" -> {
            var contact = Contact()
            supportFragmentManager.beginTransaction()
                .replace(R.id.contact_us, contact).commit()
        }
    }
}*/
