package com.gachon.footprint.data

data class ModelUser(
    var userEmail: String? = null,
    var uid: String? = null,
    //유저 프로파일 이미지-경로 저장
    var userImg: String? = null,
    var nickname: String? = null,
    // 유저 패스워드 변수 추가했음
    var password: String? = null
)
