package com.gachon.footprint.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gachon.footprint.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserFragment : Fragment() {

    var fragmentVIew : View ? = null
    var firestore : FirebaseFirestore? = null
    var uid : String?=null
    var auth : FirebaseAuth?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentVIew = LayoutInflater.from(activity).inflate(R.layout.fragment_user,container,false)




        return fragmentVIew
    }
}