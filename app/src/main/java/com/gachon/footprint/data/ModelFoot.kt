package com.gachon.footprint.data

import android.location.Location
import android.provider.MediaStore
import com.google.android.gms.maps.model.LatLng

data class ModelFoot(
    var title: String? =null,
    var uid:String?=null,
    var nickname:String?=null,
    var timestamp:Long?=null,
    var msgText: String?=null,
    //이미지 및 동영상 uri 경로 저장
    var msgImg: String?=null,
    var msgMedia: String?=null,
    val latLng: LatLng? =null, //Gps정보(위도, 경도)
    var arMarker: String?=null){

    data class Review(var uid:String?= null,
                      var nickname: String?=null,
                      var msgText: String?=null,
                      var recommend : Boolean?=null,
                      var report : Boolean? = null)

}










