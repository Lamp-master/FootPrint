package com.gachon.footprint
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gachon.footprint.data.ModelFoot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_footprint.*
import timber.log.Timber
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

//10.12해야할 것 : 이미지 받아서 사용자 uid로 새폴더 생성 후, 이미지 파일 저장
//파이어 스토어 footmsg저장경로 -> collection("FootMsg").document("랜덤uid")
// 파이어 스토어 저장시 위치정보 무조건 같이 저장 null값될시 앱다운 제일중요

class FootMsgActivity : AppCompatActivity() {

    var selectedPhotoUri: Uri? = null
    private var auth: FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser
    var uid = user?.uid
    var lat: String? = null
    var lon: String? = null

    var footmsgInfo : ModelFoot? = ModelFoot()

    private val footMsgRef = db.collection("FootMsg")
    //저장소 R/W을 받는 권한설정()
    val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    val STORAGE_PERMISSION = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val FLAG_PERM_CAMERA = 98
    val FLAG_PERM_STORAGE = 99
    val FLAG_REQ_CAMERA = 101
    val FLAG_REQ_STORAGE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_footprint)

        val fmbar = findViewById<Toolbar>(R.id.activity_footprint_toolbar)
        setSupportActionBar(fmbar)
        val ab: androidx.appcompat.app.ActionBar? = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
        ab?.title = "발자취 등록"

        Timber.plant(Timber.DebugTree())
        auth = FirebaseAuth.getInstance()
        getUserInfo()
        if(intent.hasExtra("LAT") && intent.hasExtra("LON")) {
            lat = intent.getStringExtra("LAT")
            lon = intent.getStringExtra("LON")
            Timber.d("TestGps $lat $lon")
            footmsgInfo?.latitude = lat?.toDouble()
            footmsgInfo?.longitude = lon?.toDouble()
        }


        //제목 : add_footprint_title -Title, 사진 : footprintImg - FootMedia,
        // add_footprint_location - Location : 위치정보, 내용 : add_footprint_context
        //내장실행->남긴시간(Timestamp-WrittenTime),
        //전 액티비티에서 위치정보 받고 이를 위도 경도로 표시해 주기
        //setView->openGallery->
        //권한을 받고 이미지뷰(footPrintImg)클릭해서 이미지 저장소에서 가져오기
        if (checkPermission(STORAGE_PERMISSION, FLAG_PERM_STORAGE)) { setViews()}

        //modeldata access후, firestore에 upload
        confirm_button.setOnClickListener {
            Timber.d("Test GPS $lat $lon")
            if(lat !=null && lon !=null) {
                footmsgInfo?.title = add_footprint_title.text.toString()
                //사용자 이미지 업로드
                footmsgInfo?.imageUrl = selectedPhotoUri.toString()
                upLoadImageToCloud()
                footmsgInfo?.msgText = add_footprint_context.text.toString()
                footmsgInfo?.timestamp = System.currentTimeMillis()
                Timber.d("Testinbtn ${footmsgInfo?.nickname.toString()}")
                //firestore에 push
                footmsgInfo?.let { it1 ->
                    db.collection("FootMsg").add(it1).addOnSuccessListener { documentReference ->
                        Log.d("Put", "발자취 등록 성공")
                    }
                }
                upLoadImageToCloud()
                startActivity(Intent(this, MainActivity::class.java))

            }

/*
            footmsgInfo?.uid?.let { it -> db.collection("FootMsg").add(footmsgInfo!!).addOnSuccessListener { documentReference ->
                Log.d("Put", "발자취 등록 성공")
            }
*/

                //해당 User.uid.footlist(collection)에 만들어진 footMsgId 추가.

            setResult(Activity.RESULT_OK)
/*
            db.collection("User").document(auth?.uid.toString()).set(userInfo)
                .addOnSuccessListener { void: Void? ->
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, LoginActivity::class.java))}*/
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //툴바를 가져옵니다
        menuInflater.inflate(R.menu.footprint_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //뒤로가기 버튼
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.btn_save_footprint -> {
                //저장하기 버튼

                Timber.d("Test GPS $lat $lon")
                if(lat !=null && lon !=null) {
                    footmsgInfo?.title = add_footprint_title.text.toString()
                    //사용자 이미지 업로드
                    footmsgInfo?.msgImg = selectedPhotoUri.toString()
                    upLoadImageToCloud()
                    footmsgInfo?.msgText = add_footprint_context.text.toString()
                    footmsgInfo?.timestamp = System.currentTimeMillis()
                    Timber.d("Testinbtn ${footmsgInfo?.nickname.toString()}")
                    //firestore에 push
                    footmsgInfo?.let { it1 ->
                        db.collection("FootMsg").add(it1).addOnSuccessListener { documentReference ->
                            Log.d("Put", "발자취 등록 성공")
                        }
                    }
                }

/*
            footmsgInfo?.uid?.let { it -> db.collection("FootMsg").add(footmsgInfo!!).addOnSuccessListener { documentReference ->
                Log.d("Put", "발자취 등록 성공")
            }
*/

                //해당 User.uid.footlist(collection)에 만들어진 footMsgId 추가.

                setResult(Activity.RESULT_OK)
/*
            db.collection("User").document(auth?.uid.toString()).set(userInfo)
                .addOnSuccessListener { void: Void? ->
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, LoginActivity::class.java))}*/

            }
        }
        return super.onOptionsItemSelected(item)
    }

    //이미지 저장소에서 불러오기
    private fun setViews() {
        footprintImg.setOnClickListener {
            openCamera()
        }
        footprintImg.setOnClickListener {
            openGallery()
        }
    }

    //카메라로 찍기
    private fun openCamera() {
        if (checkPermission(CAMERA_PERMISSION, FLAG_PERM_CAMERA)) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, FLAG_REQ_CAMERA)
        }
    }

    //개인 저장소 개인 갤러리 열기
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, FLAG_REQ_STORAGE)
    }

    //사진 촬영이 완료되면 Result메소드로 결과값 전달(새사진:data)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FLAG_REQ_CAMERA -> {
                    if (data?.extras?.get("data") != null) {
                        val bitmap = data?.extras?.get("data") as Bitmap
//                        imagePreview.setImageBitmap(bitmap)
                        val uri = saveImageFile(newFileName(), "image/jpg", bitmap)
                        footprintImg.setImageURI(uri)
                        //modelFoot과 바인딩 하기
                        footmsgInfo?.imageUrl = uri.toString()
                    }
                }
                FLAG_REQ_STORAGE -> {
                    val uri = data?.data
                    footprintImg.setImageURI(uri)
                    selectedPhotoUri = uri
                    footmsgInfo?.imageUrl = uri.toString()
                }
            }
        }
    }

    //이미지를 저장시키고 uri->savedPhotoUri에 할당 ????????????
    private fun saveImageFile(filename: String, mimeType: String, bitmap: Bitmap): Uri? {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        try {
            if (uri != null) {
                var descriptor = contentResolver.openFileDescriptor(uri, "w")
                if (descriptor != null) {
                    val fos = FileOutputStream(descriptor.fileDescriptor)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.close()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        values.clear()
                        values.put(MediaStore.Images.Media.IS_PENDING, 0)
                        contentResolver.update(uri, values, null, null)
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("File", "error=${e.localizedMessage}")
        }
        return uri
    }

    //사진촬영시 사진 제목을 timestamp로 하여 query 용이하게
    private fun newFileName(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())

        return "$filename.jpg"
    }


    //현재 사용자의 uid를 받아 storage 폴더에 이미지를 업로드한다.(나중에 timestamp형식으로 고치기)
    private fun upLoadImageToCloud() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/FootMsgImage/$uid/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Register", "이미지 업로드 성공 : ${it.metadata?.path}")
            }
    }
    // 현재 사용자 정보 가져오는 함수
    private fun getUserInfo(){
        if(user!=null) {
            db.collection("User").document(user!!.uid).get().addOnSuccessListener { documentSnapshot ->
                var map: Map<String, Any> = documentSnapshot.data as Map<String, Any>
                footmsgInfo?.uid = map["uid"].toString()
                footmsgInfo?.nickname = map["nickname"].toString()
                footmsgInfo?.email = map["email"].toString()
                Timber.d("Testuserinfo ${footmsgInfo?.nickname.toString()}")
            }
        }
    }
    //권한 처리 함수
    private fun checkPermission(permissions: Array<out String>, flag: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(this, permissions, flag)
                    return false
                }
            }
        }
        return true
    }

    //스토리지/카메라 퍼미션
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            FLAG_PERM_STORAGE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "저장소 권한을 승인해야지만 앱을 사용할 수 있습니다.", Toast.LENGTH_LONG)
                            .show()
                        finish()
                        return
                    }
                }
                setViews()
            }
            FLAG_PERM_CAMERA -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "카메라 권한을 승인해야지만 카메라를 사용할 수 있습니다.", Toast.LENGTH_LONG)
                            .show()
                        return
                    }
                }
                openCamera()
            }
        }
    }
}

// 할 것 timestamp/location/동영상버튼 만들어지면 동영상 처리/armarkerlist/mapmarker/

/*    private fun getCurrentTime() {
        val timestamp = it["time"] as com.google.firebase.Timestamp
        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val netDate = Date(milliseconds)
        val date = sdf.format(netDate).toString()
        Log.d("TAG170", date)
    }*/







