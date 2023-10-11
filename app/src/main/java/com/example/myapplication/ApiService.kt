package com.example.myapplication


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("get_data")
    fun getData(): Call<ResponseData>


//    @GET("status")
//    fun getStatus():Call<StatusResponseData>
//
//
//    @POST("update")
//    fun postUpdate(@Body status : StatusResponseData ):Call<StatusResponseData>

    @GET("get_cordinates")
    fun getCoordinate():Call<CoordinateResponseData>
}