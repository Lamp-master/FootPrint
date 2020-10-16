package com.gachon.footprint.settingfragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.gachon.footprint.MainActivity
import com.gachon.footprint.R
import com.gachon.footprint.SettingActivity
import com.gachon.footprint.ViewSettingActivity
import com.gachon.footprint.data.ModelFoot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.s_modify_info.*
import timber.log.Timber

class UserInfoModify : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth? = null
    var user = FirebaseAuth.getInstance().currentUser
    var footmsgInfo: ModelFoot? = ModelFoot()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.s_modify_info, container, false)
        val uimbar = view.findViewById(R.id.modify_info_toolbar) as Toolbar
        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(uimbar)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.title = "프로필 수정"
        getUserInfo()
        Timber.d("Test1 ${footmsgInfo?.nickname}")
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_modify.setOnClickListener {
            footmsgInfo?.nickname = user_in_nickname.text.toString()
            footmsgInfo?.let { it1 ->
                db.collection("User").document(footmsgInfo?.uid.toString()).set(
                    it1, SetOptions.merge())
                activity?.let{
                    val intent = Intent(context, SettingActivity::class.java)
                    startActivity(intent)

                }
            }
        }
    }



    private fun getUserInfo() {
        if (user != null) {
            db.collection("User").document(user!!.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    var map: Map<String, Any> = documentSnapshot.data as Map<String, Any>
                    footmsgInfo?.uid = map["uid"].toString()
                    footmsgInfo?.nickname = map["nickname"].toString()
                    footmsgInfo?.email = map["email"].toString()
                    Timber.d("Testemail ${footmsgInfo?.email}")
                    Timber.d("Testuid ${footmsgInfo?.uid}")
                    setContent()

                }
        }
    }

    private fun setContent() {
        Timber.d("Test4 ${footmsgInfo?.nickname}")
        user_in_email.setText(footmsgInfo?.email)
        user_in_nickname.setText(footmsgInfo?.nickname)
    }
}
/*
btn_modify.setOnClickListener {
    footmsgInfo?.nickname = user_in_nickname.text.toString()
    footmsgInfo?.let { it1 ->
        db.collection("User").document(footmsgInfo?.uid.toString()).set(
            it1, SetOptions.merge()
        )
    }
}*/

