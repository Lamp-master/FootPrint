package com.gachon.footprint.data

data class ModelFoot(
    var title: String? = null,
    var nickname: String? = null,
    var timestamp: Long? = null,
    var msgText: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var distance: Double? = null,
    var imageUrl: String? = null,
    var footMsgId: String? = null
)