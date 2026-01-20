package com.hosteldada.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.hosteldada.android.ui.theme.HostelDadaTheme

/**
 * Main Activity - Entry point for the Android app
 * 
 * Uses Compose Multiplatform for UI rendering
 * Integrates with the shared navigation system
 */
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            HostelDadaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Main Navigation Host
                    HostelDadaNavHost()
                }
            }
        }
    }
}
