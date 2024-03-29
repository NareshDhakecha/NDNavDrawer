package com.ndsoftwares.navdrawer.sample

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.ndsoftwares.navdrawer.NDDrawerLayout
import com.ndsoftwares.navdrawer.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: NDDrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            drawerLayout.openDrawer(GravityCompat.START)
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
        }
        drawerLayout = binding.drawerLayout

        // ***** All Customize *******

//        drawerLayout.setViewScale(GravityCompat.START, 0.9f); //set height scale for main view (0f to 1f)
//        drawerLayout.setViewElevation(GravityCompat.START, 20); //set main view elevation when drawer open (dimension)
        drawerLayout.setViewScrimColor(Gravity.START, Color.TRANSPARENT); //set drawer overlay color (color)
//        drawerLayout.setDrawerElevation(Gravity.START, 20); //set drawer elevation (dimension)
//        drawerLayout.setContrastThreshold(3); //set maximum of contrast ratio between white text and background color.
//        drawerLayout.setRadius(GravityCompat.START, 25); //set end container's corner radius (dimension)

        drawerLayout.setViewScale(Gravity.START, 0.8f)
        drawerLayout.setViewElevation(Gravity.START, 20f)
        drawerLayout.setRadius(Gravity.START, 35f)
        drawerLayout.setViewRotation(Gravity.START, 15f)



        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout as DrawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // ND: Required
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}