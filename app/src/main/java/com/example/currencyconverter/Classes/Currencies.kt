package com.example.currencyconverter.Classes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class Currencies(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val currencyCode: String,
    val currencyName: String,
    val currencySymbol: String? = null,
)
