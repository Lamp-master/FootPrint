package com.gachon.footprint.api

import com.gachon.footprint.model.VenueWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface FoursquareAPI {

    companion object {
        const val BASE_URL = "https://api.foursquare.com/v2/venues/"
    }

    @GET("search")
    fun searchVenues(@QueryMap params: Map<String, String>): Call<VenueWrapper>
}