package com.gachon.footprint.data

import android.app.Activity
import android.content.Context

class ModelAnchor {
    //Anchor ID를 current
    private val NEXT_SHORT_CODE = "next_short_code"
    private val KEY_PREFIX = "anchor;"
    private val INITIAL_SHORT_CODE = 142
    //앵커를 저장하기 위한 코드를 액티비티에서 가져옵니다.
    fun nextShortCode(activity: Activity):Int {
        val sharedPrefs = activity.getPreferences(Context.MODE_PRIVATE)
        val shortCode = sharedPrefs.getInt(NEXT_SHORT_CODE, INITIAL_SHORT_CODE)
        //가져온 AnchorID를 Next SHORT CODE에 저장
        sharedPrefs.edit().putInt(NEXT_SHORT_CODE, shortCode + 1)
            .apply()
        return shortCode
    }
    //액티비티의 SharedPreferences에 앵커ID 저장 ->Firestore user
    fun storeUsingShortCode(activity:Activity, shortCode:Int, cloudAnchorId:String) {
        val sharedPrefs = activity.getPreferences(Context.MODE_PRIVATE)
        sharedPrefs.edit().putString(KEY_PREFIX + shortCode, cloudAnchorId).apply()
    }
    //shortCode를 이용해서 클라우드 Anchor ID를 검색(존재하면 빈 문자열 반환)
    fun getCloudAnchorID(activity:Activity, shortCode:Int): String? {
        val sharedPrefs = activity.getPreferences(Context.MODE_PRIVATE)
        //반환 값 : anchor(앵커 ID)
        return sharedPrefs.getString(KEY_PREFIX+shortCode,"")
    }

}