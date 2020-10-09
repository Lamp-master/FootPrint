package com.gachon.footprint

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gachon.footprint.data.ModelUser
import com.gachon.footprint.data.UserDB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_enter.*
import timber.log.Timber

class UserEnterActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    /*private var userDb: UserDB? = null*/
    private val db = FirebaseFirestore.getInstance()
    /*var userInfo = ModelUser()*/
   /* private val addRunnable = Runnable {
        userInfo.uid = auth?.uid
        userInfo.userEmail = user_in_email.text.toString()
        userInfo.password = user_in_password.text.toString()
        userInfo.nickname = user_in_nickname.text.toString()
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_enter)
        /*userDb = UserDB.getInstance(this)*/
        auth = FirebaseAuth.getInstance()

        btn_in_user_db.setOnClickListener {
            createEmail()
        }
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
        userInfo.userEmail = user_in_email.text.toString()
        userInfo.password = user_in_password.text.toString()
        userInfo.nickname = user_in_nickname.text.toString()

        db.collection("User").document(auth?.uid.toString()).set(userInfo)
            .addOnSuccessListener { void: Void? ->
                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, LoginActivity::class.java))}
    }
}



