package com.example.currencyconverter.Classes

import com.google.gson.annotations.SerializedName

data class Currency(
    val result: String,
    val documentation: String,
    @SerializedName("terms_of_use")
    val termsOfUse: String,
    @SerializedName("supported_codes")
    val suportedCodes: List<List<CurrencyCode>>
)

//NEED TO IMPLEMENT A MAP ON SUPPORTED CODES AND THEN CONVERT TO LIST
data class CurrencyCode(
    val code: String,
    val name: String
)