package com.imptt.v2.view.main

import android.os.Bundle
import android.os.Message
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.imptt.v2.R
import com.imptt.v2.core.messenger.view.ViewMessenger
import com.imptt.v2.core.struct.ServiceBindActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ServiceBindActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        findViewById<FloatingActionButton>(R.id.fabHome).setOnClickListener {
            ViewMessenger.send(Message().apply {
                this.what = 666
                this.obj = Bundle()
                this.replyTo = ViewMessenger.myself()
            })
        }
    }
}