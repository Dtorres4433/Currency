package com.example.currencyconverter

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.currencyconverter.BuildConfig.API_KEY
import com.example.currencyconverter.Classes.Currency
import com.example.currencyconverter.Classes.Rates
import retrofit2.http.Path

interface ApiServices {
    @GET("{apiKey}/codes")
    fun getCurrencies(@Path("apiKey") apiKey: String = API_KEY): Call<Currency>
    @GET("{apiKey}/pair/{base}/{target}")
    fun getLatestRates(@Path("apiKey") apiKey: String = API_KEY,
                               @Path("base") base: String,
                               @Path("target") target: String ): Call<Rates>
}