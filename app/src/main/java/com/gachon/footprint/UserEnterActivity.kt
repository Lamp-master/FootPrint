package com.gachon.footprint

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.gachon.footprint.data.ModelFoot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_user_enter.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class UserEnterActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage?= null
    var photoUri: Uri?=null
    private var auth: FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance()
    private var cnt : Int = 0
    var uid = auth?.uid
    var userInfo = ModelFoot()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_enter)
        Timber.plant(Timber.DebugTree())
        /*userDb = UserDB.getInstance(this)*/
        auth = FirebaseAuth.getInstance()
        //Initiate storage
        storage = FirebaseStorage.getInstance()
        //Open the album
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        val userinbar = findViewById<Toolbar>(R.id.user_in_toolbar)
        setSupportActionBar(userinbar)
        val ab: ActionBar? = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
        ab?.title = "회원가입"
        //이미지 버튼 클릭 리스너
        btn_upload_image.setOnClickListener {
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM) } }

   override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    //add image upload envent and createEmail
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode == Activity.RESULT_OK){
                //This is path to the selected image
                photoUri = data?.data
                pic_profile.setImageURI(photoUri)
                cnt = 1
            }else {
                //Exit the addPhotoActivity if you leave the album without selection it
                finish()
            }
        }
    }
    fun contentUpload() {
        //Make filename
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"
        var storageRef = FirebaseStorage.getInstance().getReference("/ProfileImg/$imageFileName")
        //FileUpload
            photoUri?.let {
                storageRef?.putFile(it)?.addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        //Insert downloadUrl of image
                        userInfo.imageUrl = uri.toString()
                        addFireStore()
                    }
                }
            }
    }
    // 사용자가 이미지를 등록할 건지 안할건지에 따라 호출함수 다름
    fun createEmail() {
        auth?.createUserWithEmailAndPassword(
            user_in_email.text.toString(),
            user_in_password.text.toString()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if(cnt == 1) contentUpload()
                else addFireStore()
            } else {
                //에러 메세지(중복 이메일/빈 칸/양식오류)
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    // 유저정보 파이어스토어 등록
    private fun addFireStore() {
        userInfo.uid = auth?.uid
        userInfo.email = user_in_email.text.toString()
        userInfo.password = user_in_password.text.toString()
        userInfo.nickname = user_in_nickname.text.toString()
        Timber.d("Testuri ${userInfo.imageUrl}")
        db.collection("User").document(auth?.uid.toString()).set(userInfo)
            .addOnSuccessListener { void: Void? ->
                Toast.makeText(this, "회원가입이 완료되었습니다", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, LoginActivity::class.java))}
    }
}