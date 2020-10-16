package com.gachon.footprint.settingfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.gachon.footprint.R

class UserInfoModify : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.s_modify_info, container, false)

        val uimbar = view.findViewById(R.id.modify_info_toolbar) as Toolbar
        val activity = activity as AppCompatActivity?

        activity?.setSupportActionBar(uimbar)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.title = "프로필 수정"

        return view
    }
}




