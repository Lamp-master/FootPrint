package com.gachon.footprint.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gachon.footprint.R
import com.gachon.footprint.ViewSettingActivity
import com.gachon.footprint.data.CurrentUser
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.s_modify_info.*

class SettingFragment : Fragment() {
    var fragmentVIew : View ? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentVIew = LayoutInflater.from(activity).inflate(R.layout.fragment_setting,container,false)
        return fragmentVIew
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        account_info.setOnClickListener {
            activity?.let {
                val intent = Intent(context,ViewSettingActivity::class.java)
                intent.putExtra("setting","0")
                startActivity(intent)
            }
        }
        modify_info.setOnClickListener {
            activity?.let {
                val intent = Intent(context,ViewSettingActivity::class.java)
                intent.putExtra("setting","1")

                startActivity(intent)
            }
        }
        change_account.setOnClickListener {
            activity?.let {
                val intent = Intent(context,ViewSettingActivity::class.java)
                intent.putExtra("setting","2")
                startActivity(intent)
            }
        }
        withdraw_account.setOnClickListener {
            activity?.let {
                val intent = Intent(context,ViewSettingActivity::class.java)
                intent.putExtra("setting","3")
                startActivity(intent)
            }
        }
        set_location_info.setOnClickListener {
            activity?.let {
                val intent = Intent(context,ViewSettingActivity::class.java)
                intent.putExtra("setting","4")
                startActivity(intent)
            }
        }
        account_report.setOnClickListener {
            activity?.let {
                val intent = Intent(context,ViewSettingActivity::class.java)
                intent.putExtra("setting","5")
                startActivity(intent)
            }
        }
        app_info.setOnClickListener {
            activity?.let {
                val intent = Intent(context,ViewSettingActivity::class.java)
                intent.putExtra("setting","6")
                startActivity(intent)
            }
        }
        licence.setOnClickListener {
            activity?.let {
                val intent = Intent(context,ViewSettingActivity::class.java)
                intent.putExtra("setting","7")
                startActivity(intent)
            }
        }
        contact_us.setOnClickListener {
            activity?.let {
                val intent = Intent(context,ViewSettingActivity::class.java)
                intent.putExtra("setting","8")
                startActivity(intent)
            }
        }

    }
}