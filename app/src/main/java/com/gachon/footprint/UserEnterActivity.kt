package com.gachon.footprint

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gachon.footprint.data.ModelUser
import com.gachon.footprint.data.UserDB
import kotlinx.android.synthetic.main.activity_user_enter.*

class UserEnterActivity : AppCompatActivity() {
    private var userDb: UserDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_enter)
        userDb = UserDB.getInstance(this)

        val addRunnable = Runnable {
            val newUser = ModelUser()
            newUser.userName = user_in_name.text.toString()
            newUser.userEmail = user_in_email.text.toString()
            newUser.userpassword = user_in_password.text.toString()
            newUser.userNickName = user_in_nickname.text.toString()
            userDb?.userDao()?.insert(newUser)
        }

        btn_in_user_db.setOnClickListener {
            val addThread = Thread(addRunnable)
            addThread.start()

            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}
