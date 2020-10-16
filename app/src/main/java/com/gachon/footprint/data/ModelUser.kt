package com.gachon.footprint.data

import com.google.android.gms.maps.model.LatLng

data class ModelUser(
    var email: String? = null,
    var uid: String? = null,
    //유저 프로파일 이미지-경로 저장
    var Img: String? = null,
    var nickname: String? = null,
    // 유저 패스워드 변수 추가했음
    var password: String? = null,
    var latLng: LatLng? = null
)
