package com.example.currencyconverter.Classes

data class Currency(
    val code: String,
    val name: String,
)

fun convertMapToCurrencyList(currencyMap: Map<String, String>): List<Currency> {
    return currencyMap.map { (code, name) -> Currency(code, name) }
}
