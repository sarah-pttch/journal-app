package com.study.journalapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.study.journalapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setSupportActionBar(binding.toolbar)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val builder = AppBarConfiguration.Builder(setOf(
            R.id.homeFragment,
            R.id.listFragment,
            R.id.calendarFragment,
            R.id.settingsFragment
        ))
        val appBarConfiguration = builder.build()
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.fab.show()
                    binding.fab.setOnClickListener {
                        navController.navigate(R.id.action_homeFragment_to_createEntryFragment)
                    }
                }
                R.id.listFragment -> {
                    binding.fab.show()
                    binding.fab.setOnClickListener {
                        navController.navigate(R.id.action_listFragment_to_createEntryFragment)
                    }
                }
                R.id.calendarFragment -> {
                    binding.fab.show()
                    binding.fab.setOnClickListener {
                        navController.navigate(R.id.action_calendarFragment_to_createEntryFragment)
                    }
                }
                R.id.settingsFragment -> binding.fab.hide()
                R.id.createEntryFragment -> binding.fab.hide()
                R.id.viewEntryFragment -> binding.fab.hide()
            }
        }

        binding.fab.setOnClickListener {
            navController.navigate(R.id.action_global_createEntryFragment)
        }
    }
}