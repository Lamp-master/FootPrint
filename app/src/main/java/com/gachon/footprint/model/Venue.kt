package com.gachon.footprint.model

import com.google.gson.annotations.SerializedName

data class Venue(
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("lat")
    var latitude: Double? = null,
    @SerializedName("long")
    var longitude: Double? = null,
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    var timestamp: Long? = null,
    var distance: Double? = null,
    var footmsgid: String? = null
)