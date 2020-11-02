package com.gachon.footprint.settingfragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.gachon.footprint.R
import com.gachon.footprint.SettingActivity
import com.gachon.footprint.data.ModelFoot
import com.gachon.footprint.data.ModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.s_modify_info.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class UserInfoModify : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth? = FirebaseAuth.getInstance()
    var PICK_IMAGE_FROM_ALBUM = 0
    var photoUri: Uri? = null
    var storage: FirebaseStorage? = null
    private var cnt: Int = 0

    var user = FirebaseAuth.getInstance().currentUser
    var footmsgInfo: ModelUser? = ModelUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //프레그먼트에서 메뉴를 사용
        setHasOptionsMenu(true)
        Timber.plant(Timber.DebugTree())
        storage = FirebaseStorage.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.s_modify_info, container, false)

        val uimbar = view.findViewById(R.id.modify_info_toolbar) as Toolbar
        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(uimbar)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.title = ""

        getUserInfo()
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.modify_toolbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btn_modify -> {
                if (cnt == 1) contentUpload()
                else addFireStore()
            }
        }
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        pic_profile.setOnClickListener {
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }
    }

    //프로필 수정에서 사진을 수정할시 호출
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                //This is path to the selected image
                photoUri = data?.data
                pic_profile.setImageURI(photoUri)
                cnt = 1
            } else {
                //Exit the addPhotoActivity if you leave the album without selection it
            }
        }
    }

    //사진이 업로드(수정)되었을 경우 storage에 업로드해줌
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
                    footmsgInfo?.imageUrl = uri.toString()
                    addFireStore()
                }
            }
        }
    }

    //파이어스토어에 업데이트된 정보 저장
    private fun addFireStore() {
        footmsgInfo?.uid = auth?.uid
        footmsgInfo?.email = user_in_email.text.toString()
        footmsgInfo?.nickname = user_in_nickname.text.toString()
        footmsgInfo?.let {
            db.collection("User").document(auth?.uid.toString()).set(it, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(context, "수정이 완료되었습니다.", Toast.LENGTH_LONG).show()
                    activity.let {
                        val intent = Intent(context, SettingActivity::class.java)
                        startActivity(intent)
                    }

                }
        }
    }

    //파이어 스토어로부터 현재 유저 정보 받아옴
    private fun getUserInfo() {
        if (user != null) {
            db.collection("User").document(user!!.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    footmsgInfo = documentSnapshot.toObject(ModelUser::class.java)
                    setContent()
                }
        }
    }

    //레이아웃에 현재 유저 정보 보여줌
    private fun setContent() {
        user_in_email.setText(footmsgInfo?.email)
        user_in_nickname.setText(footmsgInfo?.nickname)
        if (footmsgInfo?.imageUrl != null) {
            this.context?.let {
                Glide.with(it).load(footmsgInfo?.imageUrl).override(300, 225).into(pic_profile)
            }
        }
    }
}
