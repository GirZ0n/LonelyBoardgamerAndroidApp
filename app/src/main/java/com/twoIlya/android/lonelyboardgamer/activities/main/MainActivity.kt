package com.twoIlya.android.lonelyboardgamer.activities.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.twoIlya.android.lonelyboardgamer.R
import com.twoIlya.android.lonelyboardgamer.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        navController = findNavController(R.id.myNavHostFragment)

        startDestinationSetup()

        appBarConfigurationSetup()

        setupDrawerLayout()
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun startDestinationSetup() {
        val navHostFragment = myNavHostFragment as NavHostFragment
        val navInflater = navHostFragment.navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.navigation)

        val destination = when (intent.getBooleanExtra(EXTRA_IS_REGISTRATION_NEEDED, false)) {
            true -> R.id.registrationFragment
            else -> R.id.myProfileFragment
        }

        navGraph.startDestination = destination
        navController.graph = navGraph
    }

    private fun appBarConfigurationSetup() {
        val topLevelDestinations =
            setOf(
                R.id.myProfileFragment,
                R.id.searchFragment,
                R.id.friendsListFragment,
                R.id.banListFragment,
                R.id.outRequestsFragment,
                R.id.inRequestsFragment,
                R.id.friendsListFragment
            )
        appBarConfiguration = AppBarConfiguration(topLevelDestinations, binding.drawerLayout)
    }

    private fun setupDrawerLayout() {
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    companion object {
        private const val EXTRA_IS_REGISTRATION_NEEDED =
            "com.twoIlya.android.lonelyboardgamer.activities.main.is_registration_needed"

        fun newActivity(context: Context, isRegistrationNeeded: Boolean): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(EXTRA_IS_REGISTRATION_NEEDED, isRegistrationNeeded)
            }
        }
    }
}
