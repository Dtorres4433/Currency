package com.example.currencyconverter

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.currencyconverter.Classes.Currencies

@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCurrency(currencyTable: Currencies)
    @Update
    fun updateCurrency(currencyTable: Currencies)
    @Query("SELECT * FROM currencies")
    fun getAllCurrencies(): LiveData<List<Currencies>>
}