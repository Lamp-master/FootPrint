package com.gachon.footprint.data

import android.content.Context
import android.location.Location
import com.google.firebase.firestore.FirebaseFirestore

//사용자의 uid와 위치정보를 받아서 파이어스토어에 메세지를 CRUD
public class FootAdapter(val uid:String?=null,var location : Location?=null ) {
    val db = FirebaseFirestore.getInstance()
    val FootMsgRef=db.collection()
    val DocumentRef=db.collection("FootMsg").document("")
    val androidTutRef=db.collection("FootMsg").document()
    fun CreateFootMsg(){
        .document()
    }


    //발자취메세지 Create Read Update Delete
    //get : 사용자 uid,location / set : FootMsg -> User collection FootList/CreateDocument
    fun CreateFootMsg(){
        fire

    }

    //get : 사용자 위치정보, AR? /set : FootMsg
    fun ReadFootMsg(){}


    fun UpdateFootMsg(){}

    fun DeleteFootMsg(){}

    //발자취메세지 내 Document Create Read Update Delete
    fun  CreateMsgText(){}
    fun  ReadMsgText(){}
   // fun  UpdateMsgText(){} - 자신의 메세지 수정
   // fun  DeleteMsgText(){} - 발자취 삭제시 자동삭제

    fun




}