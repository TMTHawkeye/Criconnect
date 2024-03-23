package com.example.criconnect

import android.app.Application
import com.example.criconnect.DependencyInjection.mainModule
import com.google.firebase.FirebaseApp
import io.paperdb.Paper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Add this code in your Application class or MainActivity onCreate method
        FirebaseApp.initializeApp(this)
        Paper.init(applicationContext)
        startKoin {
            androidContext(this@MainApplication)
            modules(mainModule)
        }
    }
}