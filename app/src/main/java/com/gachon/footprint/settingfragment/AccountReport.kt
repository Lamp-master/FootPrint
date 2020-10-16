package com.gachon.footprint.settingfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.gachon.footprint.R

class AccountReport : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       val view = inflater.inflate(R.layout.s_account_report, container, false)
       val arbar = view.findViewById(R.id.account_report_toolbar) as Toolbar
       val activity = activity as AppCompatActivity?

       activity?.setSupportActionBar(arbar)
       activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
       activity?.supportActionBar?.title = "계정 신고"

       return view
    }
}





