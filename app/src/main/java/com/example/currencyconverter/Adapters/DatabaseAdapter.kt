package com.example.currencyconverter.Adapters

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.currencyconverter.Classes.Currencies
import com.example.currencyconverter.CurrencyDao

@Database(entities = [Currencies::class], version = 2, exportSchema = false)
abstract class DatabaseAdapter: RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
    companion object {
        @Volatile
        private var INSTANCE: DatabaseAdapter? = null
        fun getDatabase(context: Context): DatabaseAdapter {
            return INSTANCE?:
                synchronized(this) {
                    val instance = INSTANCE ?: Room.databaseBuilder(
                                        context.applicationContext,
                                        DatabaseAdapter::class.java,
                                        "currency_database"
                                    ).fallbackToDestructiveMigration(false)
                        .build()
                    INSTANCE = instance
                    instance
                }

        }
    }
}