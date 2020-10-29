package com.softdesign.sosapplication.utils.network

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class API {

    interface APIService {

        @Headers(value = ["Content-type: application/json"])
        @GET("/login/{user}")
        fun authUser(@Path(value = "user") user: String , @Body body: JsonObject): Call<ResponseBody>

        @Headers(value = ["Content-type: application/json"])
        @POST("/login")
        fun registrationUser(@Body body: JsonObject): Call<ResponseBody>
    }

    companion object Builder {
        private val retrofit = Retrofit.Builder()
                .baseUrl("")
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()

        var service = retrofit.create(API::class.java)
    }
}