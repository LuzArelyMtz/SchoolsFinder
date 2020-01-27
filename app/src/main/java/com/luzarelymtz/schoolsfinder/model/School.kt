package com.luzarelymtz.schoolsfinder.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class School() : Parcelable {
    private lateinit var name:String
    private lateinit var address:String
    private var lat:Double=0.0
    private var lng:Double=0.0
    private var photo_width:Int=0
    private var photo_height:Int=0
    private lateinit var photo_reference:String
    private var openNow:Boolean=false
    private var rating:Double= 0.0


    constructor(name: String, address:String, lat:Double,lng:Double,photo_width:Int,photo_height:Int,photo_reference:String,openNow:Boolean,rating:Double) : this() {
        this.name = name
        this.address=address
        this.lat=lat
        this.lng=lng
        this.photo_width=photo_width
        this.photo_height=photo_height
        this.photo_reference=photo_reference
        this.openNow=openNow
        this.rating=rating
    }

    fun getName(): String? {
        return name
    }

    fun getAddress(): String? {
        return address
    }
    fun getlat(): Double? {
        return lat
    }

    fun getlng(): Double? {
        return lng
    }

    fun photo_width(): Int? {
        return photo_width
    }
    fun photo_height(): Int? {
        return photo_height
    }
    fun photo_reference(): String? {
        return photo_reference
    }
    fun openNow(): Boolean? {
        return openNow
    }
    fun rating(): Double? {
        return rating
    }
}