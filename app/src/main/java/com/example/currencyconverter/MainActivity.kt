package com.example.currencyconverter

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
import androidx.fragment.app.FragmentContainerView
import com.example.currencyconverter.Fragments.CurrencyFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

lateinit var drawerLayout: DrawerLayout
lateinit var fragentView: FragmentContainerView
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
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val tittleId: Int = getTiitle(item)
        showFragment(tittleId)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun showFragment(@StringRes tittleId: Int){
        val fragment = CurrencyFragment.newInstance()
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
}


