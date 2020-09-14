package com.altimetrik.network

import com.altimetrik.model.ResponseData
import retrofit2.http.GET

interface ApiInterface {

    @GET("?term=all")
    fun fetchData(): retrofit2.Call<ResponseData>
}