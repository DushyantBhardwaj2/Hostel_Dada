package com.hosteldada.android

import android.app.Application
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Hostel Dada Application class
 * 
 * Initializes:
 * - Firebase SDK
 * - Koin Dependency Injection
 * - Analytics and Crash reporting
 */
class HostelDadaApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Initialize Koin DI
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@HostelDadaApp)
            modules(
                appModule,
                firebaseModule,
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
        }
    }
}
