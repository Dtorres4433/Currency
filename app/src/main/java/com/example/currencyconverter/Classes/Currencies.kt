package com.example.currencyconverter.Classes

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "currencies", indices = [Index(value = ["currencyCode"], unique = true)])
data class Currencies(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val currencyCode: String,
    val currencyName: String,
    val currencySymbol: String? = null
)
