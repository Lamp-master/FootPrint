package com.gachon.footprint.data

import com.google.android.gms.maps.model.LatLng

data class ModelUser(
    var email: String? = null,
    var password: String? = null,
    var uid: String? = null,
    var nickname: String? = null,
    var imageUrl: String? = null,
    var latitude : String? = null,
    var longitude: String? = null
)
