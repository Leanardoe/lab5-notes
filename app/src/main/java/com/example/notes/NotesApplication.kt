package com.example.notes

import android.app.Application
import com.example.notes.data.AppContainer
import com.example.notes.data.AppDataContainer

class NotesApplication: Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}