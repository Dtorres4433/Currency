package com.example.currencyconverter.Classes

data class Rates(
    val result: String,
    val documentation: String,
    val termsOfUse: String,
    val timeLastUpdateUnix: Long,
    val timeLastUpdateUtc: String,
    val timeNextUpdateUnix: Long,
    val timeNextUpdateUtc: String,
    val base_code: String,
    val target_code: String,
    val conversion_rate: Double
)
