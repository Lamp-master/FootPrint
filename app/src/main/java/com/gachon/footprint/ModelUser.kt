package com.gachon.footprint


data class ModelUser (var userEmail : String? =null,
                      var userId : String? = null,
    //유저 프로파일 이미지-경로 저장
                      var userImg : String? = null,
                      var nickname : String? = null
)