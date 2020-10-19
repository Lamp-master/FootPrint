package com.gachon.footprint.settingfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.gachon.footprint.R

class Licence : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.s_licence, container, false)
        val licencebar = view.findViewById(R.id.licence_toolbar) as Toolbar
        val activity = activity as AppCompatActivity?

        activity?.setSupportActionBar(licencebar)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.title = "라이선스"
        return view

    }
}



