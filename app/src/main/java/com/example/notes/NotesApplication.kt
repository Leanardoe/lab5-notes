package com.example.notes

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.notes.data.AppContainer
import com.example.notes.data.AppDataContainer
import com.example.notes.data.UserPreferencesRepository

const val LAYOUT_PREFERENCE_NAME = "layout_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCE_NAME
)

class NotesApplication: Application() {

    lateinit var userPreferencesRepository: UserPreferencesRepository
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(dataStore)
        container = AppDataContainer(this)
    }
}