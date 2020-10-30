package com.gachon.footprint.model.converter

import com.gachon.footprint.model.Venue
import com.gachon.footprint.model.VenueWrapper
import com.google.gson.*
import java.lang.reflect.Type

class VenueTypeConverter : JsonDeserializer<VenueWrapper> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): VenueWrapper {
        val responseObject = json.asJsonObject

        if (validateResponse(responseObject)) {
            return VenueWrapper(getVenues(responseObject!!))
        }
        return (VenueWrapper(listOf()))
    }

    private fun validateResponse(json: JsonObject?): Boolean {
        return json != null && json.asJsonObject.getAsJsonObject("meta").getAsJsonPrimitive("code").asString == "200"
    }

    @Throws(JsonParseException::class)
    private fun getVenues(responseObject: JsonObject): List<Venue> {
        val venues = responseObject.getAsJsonObject("response").getAsJsonArray("venues")
        return venues.map<JsonElement, Venue> { getVenue(it.asJsonObject) }
    }

    @Throws(AssertionError::class)
    private fun getVenue(rawVenue: JsonObject): Venue {

        val title = rawVenue.getAsJsonPrimitive("title").asString
        val location = rawVenue.getAsJsonObject("location")

        val latitude = location.getAsJsonPrimitive("lat").asDouble
        val longitude = location.getAsJsonPrimitive("lng").asDouble

        val categories = rawVenue.getAsJsonArray("categories").get(0).asJsonObject

        val imageUrl = categories.getAsJsonObject("imageUrl").getAsJsonPrimitive("prefix").asString + "64.png"

        return Venue(title, latitude, longitude, imageUrl)
    }
}