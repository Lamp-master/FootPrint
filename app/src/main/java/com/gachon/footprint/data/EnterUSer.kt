package com.gachon.footprint.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//e..000 --> 회원가입시 사용하는 아이디값
@Entity
class EnterUSer(@PrimaryKey(autoGenerate = true) val euserId: Long?,
                @ColumnInfo(name="useremail") var euserEmail : String?,
                @ColumnInfo(name="usernickname") var euserNickName : String?,
                @ColumnInfo(name="userpassword") var euserpassword: String?){
    constructor() : this(null,null,null,null)
}


