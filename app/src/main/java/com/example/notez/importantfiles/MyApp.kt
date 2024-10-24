package com.example.notez.importantfiles
import android.app.Application
import com.example.notez.bookmark.BookmarkViewModel
import com.example.notez.database.AppDatabase
import com.example.notez.database.SubjectDao

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

import org.koin.androidx.viewmodel.dsl.viewModel


class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize Koin
        startKoin {
            androidContext(this@MyApp) // Provide the Android context to Koin
            modules(appModule) // Load the Koin modules
        }
    }
}

// Define your Koin modules
val appModule = module {
    single { FirebaseAuth.getInstance() } // Provide FirebaseAuth instance
    single { AppDatabase.getDatabase(androidContext()) } // Provide AppDatabase instance
    single { get<AppDatabase>().subjectDao() }
    single { get<AppDatabase>().moduleDao()}
    single { get<AppDatabase>().bookmarkDao() }
    viewModel { AuthViewModel(get(), get(), get()) }
    viewModel { BookmarkViewModel(get()) }// Provide AuthViewModel instance with FirebaseAuth
}