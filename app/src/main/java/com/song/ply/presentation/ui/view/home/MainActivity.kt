package com.song.ply.presentation.ui.view.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this@MainActivity).widthSizeClass
            // Compact - <600dp - Phone portrait
            // Medium - <=600dp and <840dp - Tablet portrait and Most large unfolded inner display in portrait
            // Expanded - >=840dp - Most large unfolded inner display in landscape

            MainNavHost(this@MainActivity)
        }
    }
}