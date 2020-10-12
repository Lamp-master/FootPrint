package com.gachon.footprint.data

import android.location.Location
import android.provider.MediaStore

data class ModelFoot(
    var title: String? =null,
    var uid:String?=null,
    var nickname:String?=null,
    var timestamp:Long?=null,
    var msgText: String?=null,
    //이미지 및 동영상 uri 경로 저장
    var msgImg: String?=null,
    var msgMedia: String?=null,
    var location: Location?=null,//받는 거 어케 받지
    var arMarker: String?=null){

    data class Review(var uid:String?= null,
                      var nickname: String?=null,
                      var msgText: String?=null,
                      var recommend : Boolean?=null,
                      var report : Boolean? = null)

}










