package com.example.currencyconverter.Classes

import com.google.gson.annotations.SerializedName

data class Currency(
    val result: String,
    val documentation: String,
    @SerializedName("terms_of_use")
    val termsOfUse: String,
    @SerializedName("supported_codes")
    val suportedCodes: Map<String, String>
){
    fun toCurrencyList(): List<CurrencyCode> {
        return convertMapToCurrencyList(suportedCodes)
    }
}

fun convertMapToCurrencyList(currencyMap: Map<String, String>): List<CurrencyCode> {
    return currencyMap.map { (code, name) -> CurrencyCode(code, name) }
}

data class CurrencyCode(
    val code: String,
    val name: String
)