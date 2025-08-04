package com.example.currencyconverter

import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.currencyconverter.Adapters.DatabaseAdapter
import com.example.currencyconverter.Adapters.RetrofitAdapter
import com.example.currencyconverter.Classes.Currencies
import com.example.currencyconverter.Classes.convertMapToCurrencyList
import com.example.currencyconverter.Fragments.CurrencyFragment
import com.example.currencyconverter.Fragments.ExchangeFragment
import com.example.currencyconverter.Fragments.HistorialFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

lateinit var drawerLayout: DrawerLayout
lateinit var navigationView: NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var toolbar: MaterialToolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val toogle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()
        //Initialize the database
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setNavigationView()
        getCurrencies()
    }

    private fun getCurrencies() {
        val retrofitAdapter = RetrofitAdapter()
        val apiService = retrofitAdapter.getRetrofit().create(ApiServices::class.java)
        val call = apiService.getCurrencies()
        call.enqueue(object: Callback<Map<String, String>>{
            override fun onResponse(call: Call<Map<String, String>?>, response: Response<Map<String, String>?>) {
                val currecyDB = DatabaseAdapter.getDatabase(this@MainActivity)
                if (response.isSuccessful) {
                    // HTTP status code is in the 200-299 range
                    val responseData: Map<String, String>? = response.body()
                    if (responseData != null) {
                        Log.d("API_SUCCESS", "Data received: $responseData")
                        val currencyList = convertMapToCurrencyList(responseData)
                        for (currency in currencyList) {
                            val currencies = Currencies(0,currency.code, currency.name,
                                getCurrencySymbol(currency.code).toString()
                            )
                            GlobalScope.launch(Dispatchers.IO) {
                                currecyDB.currencyDao().insertCurrency(currencies)
                            }
                        }
                    } else {
                        Log.w("API_SUCCESS_NO_BODY", "Response successful but body was null. Code: ${response.code()}")
                    }
                } else {
                    val errorCode = response.code()
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "Request failed with code: $errorCode. Error body: $errorBody")

                    when (errorCode) {
                        400 -> {
                            Log.e("API_ERROR_400", "Bad Request: $errorBody")
                        }
                        401 -> {
                            Log.e("API_ERROR_401", "Unauthorized: $errorBody")
                        }
                        403 -> {
                            Log.e("API_ERROR_403", "Forbidden: $errorBody")
                        }
                        404 -> {
                            Log.e("API_ERROR_404", "Not Found: $errorBody")
                        }
                        // Server Errors (5xx)
                        500 -> {
                            Log.e("API_ERROR_500", "Internal Server Error: $errorBody")
                        }
                        502 -> {
                            Log.e("API_ERROR_502", "Bad Gateway: $errorBody")
                        }
                        503 -> {
                            Log.e("API_ERROR_503", "Service Unavailable: $errorBody")
                        }

                        else -> {
                            Log.w("API_ERROR_UNKNOWN", "Unhandled error code: $errorCode. Body: $errorBody")
                        }
                    }
                }
            }

            private fun getCurrencySymbol(currencyCode: String) {
                java.util.Currency.getInstance(currencyCode).symbol
            }

            override fun onFailure(call: Call<Map<String, String>?>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }


    /*Initializes the NavigationView and sets its item selected listener.*/
    private fun setNavigationView() {
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val tittleId: Int = getTiitle(item)
        showFragment(tittleId)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun showFragment(@StringRes tittleId: Int){
        val fragment = when (tittleId) {
            R.string.converter -> CurrencyFragment.newInstance()
            R.string.exchange_from_photo -> ExchangeFragment.newInstance()
            R.string.historial -> HistorialFragment.newInstance()
            else -> throw IllegalArgumentException("Unknown title ID: $tittleId")
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_view, fragment)
            .commit()
    }
    private fun getTiitle(menuItem: MenuItem): Int {
        return when (menuItem.itemId) {
            R.id.nav_converter -> R.string.converter
            R.id.nav_photo -> R.string.exchange_from_photo
            R.id.nav_history -> R.string.historial
            else -> throw IllegalArgumentException("Unknown menu item: ${menuItem.itemId}")
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        @Suppress("DEPRECATION")
        val activeNetwork = cm.activeNetworkInfo
        @Suppress("DEPRECATION")
        return activeNetwork != null && activeNetwork.isConnected
    }
}


