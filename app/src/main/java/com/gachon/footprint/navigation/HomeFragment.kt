package com.gachon.footprint.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gachon.footprint.MapActivity
import com.gachon.footprint.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    var fragmentVIew : View ? =null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         fragmentVIew = LayoutInflater.from(activity).inflate(R.layout.fragment_home,container,false)

    return fragmentVIew
    }
}