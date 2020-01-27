package com.luzarelymtz.schoolsfinder.PlaceApi

import com.luzarelymtz.schoolsfinder.PlaceApi.services.SchoolResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Url

interface MapsApiService {

    @GET
    fun getSchools(@Url url: String): Observable<SchoolResponse>
}
