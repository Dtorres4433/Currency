package com.example.currencyconverter.Classes

data class Currency(
    val result: String,
    val documentation: String,
    val termsOfUse: String,
    val supportedCodes: List<CurrencyCode>
)
data class CurrencyCode(
    val code: String,
    val name: String
)