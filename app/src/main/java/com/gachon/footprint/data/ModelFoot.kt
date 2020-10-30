package com.gachon.footprint.data

data class ModelFoot(
    var title: String? = null,
    var email: String? = null,
    var uid: String? = null,
    var password: String? = null,
    var nickname: String? = null,
    var timestamp: Long? = null,
    var msgText: String? = null,
    //위치저장변수 , 거리변수
    var latitude: Double? = null,
    var longitude: Double? = null,
    var distance: Double? = null,
    //이미지 및 동영상 uri 경로 저장
    var imageUrl: String? = null,
    var msgMedia: String? = null,
    var arMarker: String? = null,
    var footMsgId: String? = null
) {

    data class Review(
        var uid: String? = null,
        var nickname: String? = null,
        var msgText: String? = null,
        var recommend: Boolean? = null,
        var report: Boolean? = null
    )

}










