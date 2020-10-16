package com.gachon.footprint

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.gachon.footprint.data.ModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_enter.*

class UserEnterActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_enter)
        /*userDb = UserDB.getInstance(this)*/
        auth = FirebaseAuth.getInstance()

/*        btn_in_user_db.setOnClickListener {
            createEmail()
        }*/

        val userinbar = findViewById<Toolbar>(R.id.user_in_toolbar)
        setSupportActionBar(userinbar)
        val ab: androidx.appcompat.app.ActionBar? = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
        ab?.title = "회원가입"
    }

   override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            R.id.btn_sign_in -> {
                createEmail()
                return true
            }
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun createEmail() {
        auth?.createUserWithEmailAndPassword(
            user_in_email.text.toString(),
            user_in_password.text.toString()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                addFireStore()
            } else {
                //에러 메세지(중복 이메일/빈 칸/양식오류)
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addFireStore() {
        var userInfo = ModelUser()
        userInfo.uid = auth?.uid
        userInfo.email = user_in_email.text.toString()
        userInfo.password = user_in_password.text.toString()
        userInfo.nickname = user_in_nickname.text.toString()

        db.collection("User").document(auth?.uid.toString()).set(userInfo)
            .addOnSuccessListener { void: Void? ->
                Toast.makeText(this, "회원가입이 완료되었습니다", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, LoginActivity::class.java))}
    }
}