package com.twoIlya.android.lonelyboardgamer.activities.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.twoIlya.android.lonelyboardgamer.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = myNavHostFragment as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.navigation)
        val navController = navHostFragment.navController

        val destination = when (intent.getBooleanExtra(EXTRA_IS_REGISTRATION_NEEDED, false)) {
            true -> R.id.registrationFragment
            else -> R.id.myProfileFragment
        }

        navGraph.startDestination = destination
        navController.graph = navGraph
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
