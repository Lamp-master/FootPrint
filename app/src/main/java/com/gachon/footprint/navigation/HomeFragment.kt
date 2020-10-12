package com.gachon.footprint.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gachon.footprint.CameraActivity
import com.gachon.footprint.MapActivity
import com.gachon.footprint.R
import com.gachon.footprint.ViewSettingActivity
import kotlinx.android.synthetic.main.activity_viewsetting.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_setting.*

class HomeFragment : Fragment() {
    var fragmentVIew : View ? =null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         fragmentVIew = LayoutInflater.from(activity).inflate(R.layout.fragment_home,container,false)
        return fragmentVIew
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

/*      // 발자취 추가하기 액티비티
        add_footprint.setOnClickListener {
            activity?.let {
                val intent = Intent(context, FootprintActivity::class.java)
                intent.putExtra("add", "0")
                startActivity(intent)
            }
        }*/

        find_footprint.setOnClickListener {
            activity?.let {
                val intent = Intent(context, CameraActivity::class.java)
                intent.putExtra("footprint", "1")
                startActivity(intent)
            }
        }

        near_footprint.setOnClickListener {
            activity?.let {
                val intent = Intent(context, MapActivity::class.java)
                intent.putExtra("near", "2")
                startActivity(intent)
            }
        }

/*        //다이어리 액티비티
        my_diary.setOnClickListener {
            activity?.let {
                val intent = Intent(context, DiaryActivity::class.java)
                intent.putExtra("mydiary", "3")
                startActivity(intent)
            }
        }*/


/*        //유료구매 액티비티
        buy_goods.setOnClickListener {
            activity?.let {
                val intent = Intent(context, GoodsActivity::class.java)
                intent.putExtra("near", "4")
                startActivity(intent)
            }
        }*/

        setting.setOnClickListener {
            activity?.let {
                val intent = Intent(context, ViewSettingActivity::class.java)
                intent.putExtra("near", "2")
                startActivity(intent)
            }
        }


    }
}

