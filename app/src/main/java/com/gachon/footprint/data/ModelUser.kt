package com.gachon.footprint.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ModelUser(@PrimaryKey(autoGenerate = true) val userId: Long?,
                @ColumnInfo(name="username") var userName : String?,
                @ColumnInfo(name="useremail") var userEmail : String?,
                @ColumnInfo(name="usernickname") var userNickName : String?,
                @ColumnInfo(name="userpassword") var userpassword: String?){
    constructor() : this(null,null,null,null,null)
}







/*
data class ModelUser(
    var userEmail: String? = null,
    var userId: String? = null,
    //유저 프로파일 이미지-경로 저장
    var userImg: String? = null,
    var nickname: String? = null
)*/
