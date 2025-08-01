package com.example.currencyconverter

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.currencyconverter.BuildConfig.API_KEY

interface ApiServices {
    @GET("currenciesd.json")
    fun getCurrencies(@Query("access_key") accessKey: String = API_KEY): Call<Map<String, String>>
}