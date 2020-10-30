package com.gachon.footprint

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_setting.*
import timber.log.Timber

class SettingActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        auth = FirebaseAuth.getInstance()
        Timber.plant(Timber.DebugTree())

        val settingbar = findViewById<Toolbar>(R.id.setting_toolbar)
        setSupportActionBar(settingbar)
        val abar: androidx.appcompat.app.ActionBar? = supportActionBar
        abar?.setDisplayHomeAsUpEnabled(true)
        abar?.title = "설정"

        app_info.setOnClickListener {
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
            val alertdialogs = AlertCustomDialog.AlertCustomDialogBuild()

                .setTitle("계정 변경")
                .setAlertContext("로그인 화면으로 이동합니다.\n계정을 변경하시겠습니까?")
                .setPositive("닫기")
                .setNegative("확인")
                .setButtonClick(object : AlertCustomDialog.AlertCustomDialogListener {
                    override fun clickPositive() {
                    }

                    override fun clickNegative() {
                        loggedOut()
                        //로그인 화면으로
                        Toast.makeText(this@SettingActivity, "로그아웃 되었습니다", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this@SettingActivity, LoginActivity::class.java)
                        finish()
                        startActivity(intent)
                    }
                }).create()
            alertdialogs.show(supportFragmentManager, alertdialogs.tag)
        }
        set_location_info.setOnClickListener {
            val intent = Intent(this, ViewSettingActivity::class.java)
            intent.putExtra("setting", "3")
            startActivity(intent)
        }
        account_report.setOnClickListener {
            val intent = Intent(this, ViewSettingActivity::class.java)
            intent.putExtra("setting", "4")
            startActivity(intent)
        }
        licence.setOnClickListener {
            val intent = Intent(this, ViewSettingActivity::class.java)
            intent.putExtra("setting", "5")
            startActivity(intent)
        }
        contact_us.setOnClickListener {
            val intent = Intent(this, ViewSettingActivity::class.java)
            intent.putExtra("setting", "6")
            startActivity(intent)
        }
        log_out.setOnClickListener {
            loggedOut()
            Toast.makeText(this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()

            //로그인 화면으로
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        withdraw_account.setOnClickListener {
            withdraw()
            Toast.makeText(this, "계정 탈퇴 되었습니다", Toast.LENGTH_SHORT).show()

            //로그인 화면으로
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun loggedOut() {
        FirebaseAuth.getInstance().signOut()
    }

    fun withdraw() {
        auth?.currentUser?.delete()
    }
}
