package com.twoIlya.android.lonelyboardgamer.activities.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
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

        /*
        val destination = if (intent.getBooleanExtra(
                IS_PRIVACY_POLICY_ACCEPTED,
                false
            )
        ) R.id.homeFragment else R.id.newPrivacyPolicyFragment
        */

        navGraph.startDestination = R.id.registrationFragment
        navController.graph = navGraph
    }
}