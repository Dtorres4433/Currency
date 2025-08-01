package com.example.currencyconverter.Adapters

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitAdapter {
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://openexchangerates.org/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}