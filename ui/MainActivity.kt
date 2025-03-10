package com.example.planitgo_finalproject.ui

import android.os.Bundle
import android.util.Log

import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions

import androidx.navigation.findNavController

import androidx.navigation.ui.setupWithNavController
import com.example.planitgo_finalproject.R
import com.example.planitgo_finalproject.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.post {
            val navController = findNavController(R.id.nav_host_fragment)
            binding.bottomNavigation.setupWithNavController(navController)
            binding.bottomNavigation.setOnItemSelectedListener { item ->
                navController.navigate(item.itemId, null, NavOptions.Builder()
                    .setEnterAnim(R.anim.from_right)
                    .setExitAnim(R.anim.to_left)
                    .setPopEnterAnim(R.anim.from_left)
                    .setPopExitAnim(R.anim.to_right)
                    .setPopUpTo(R.id.destinationFragment, inclusive = false)
                    .setLaunchSingleTop(true)
                    .build()
                )
                true
            }
        }
    }


    fun hideBottomNavigation(){
        binding.bottomNavigation.visibility = View.GONE
    }
    fun showBottomNavigation(){
        binding.bottomNavigation.visibility = View.VISIBLE
    }
}