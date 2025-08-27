package com.example.playlistmaker.ui.root

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding
import com.example.playlistmaker.ui.mediatec.NewPlaylistFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.core.qualifier._q


class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.getStringExtra("open_fragment") == "new_playlist") {
            supportFragmentManager.beginTransaction()
                .replace(R.id.rootFragmentContainerView, NewPlaylistFragment())
                .addToBackStack(null)
                .commit()
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavMenu = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavMenu.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            when (destination.id) {
                R.id.playlistsFragment, R.id.playerActivity, R.id.newPlaylistFragment, R.id.playlistFragment -> {
                    bottomNavMenu.isVisible = false
                }

                else -> {
                    bottomNavMenu.isVisible = true
                }
            }
        }

    }


}