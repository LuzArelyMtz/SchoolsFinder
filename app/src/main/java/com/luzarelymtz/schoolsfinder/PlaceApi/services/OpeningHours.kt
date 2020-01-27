package com.luzarelymtz.schoolsfinder.PlaceApi.services

import com.google.gson.annotations.SerializedName

data class OpeningHours(
    @SerializedName("open_now")
    val openNow: Boolean
)