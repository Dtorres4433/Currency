package com.example.currencyconverter

import android.net.ConnectivityManager
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.currencyconverter.Fragments.CurrencyFragment
import com.example.currencyconverter.Fragments.ExchangeFragment
import com.example.currencyconverter.Fragments.HistorialFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setNavigationView()
    }


    /**
     * Initializes the NavigationView and sets its item selected listener.
     */
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


