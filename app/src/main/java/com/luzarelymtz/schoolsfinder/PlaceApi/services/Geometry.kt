package com.luzarelymtz.schoolsfinder.PlaceApi.services


import com.google.gson.annotations.SerializedName

data class Geometry(
    @SerializedName("location")
    val location: Location,
    @SerializedName("viewport")
    val viewport: Viewport
)