package com.example.myapplication

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

//    http://13.233.46.189:5000/get_data
//    http://13.235.138.32:5000/get_cordinates

    private val retrofit by lazy {
        Retrofit.Builder().baseUrl(" http://13.233.46.189:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        }
    val  apiInterface by lazy {
        retrofit.create(ApiService::class.java)
    }



    private val retrofit3 by lazy {
        Retrofit.Builder().baseUrl("http://13.235.138.32:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val  apiInterface3 by lazy {
        retrofit3.create(ApiService::class.java)
    }
}