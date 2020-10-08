package com.gachon.footprint.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//e..000 --> 회원가입시 사용하는 아이디값
// userid = Room 함수 내 고유 식별자 키
// useruid = FireStore 고유 식별 UID 키
@Entity(tableName = "user")
class CurrentUser(@PrimaryKey(autoGenerate = true) val userId: Long?,
                  @ColumnInfo(name="useremail") var email : String?,
                  @ColumnInfo(name="useruid") var uid : String?,
                  @ColumnInfo(name="userImg") var Img : String?,
                  @ColumnInfo(name="usernickname") var nickname : String?,
                  @ColumnInfo(name="userpassword") var password: String?){
    constructor() : this(null,null,null,null, null,null)
}



