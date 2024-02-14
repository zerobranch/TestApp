package com.github.zerobranch.beebox.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPrefsDatastore(private val dataStore: DataStore<Preferences>) {
    companion object {
        fun stringKey(name: String) = stringPreferencesKey(name)
        fun intKey(name: String) = intPreferencesKey(name)
        fun doubleKey(name: String) = doublePreferencesKey(name)
        fun booleanKey(name: String) = booleanPreferencesKey(name)
        fun floatKey(name: String) = floatPreferencesKey(name)
        fun longKey(name: String) = longPreferencesKey(name)
        fun stringSetKey(name: String) = stringSetPreferencesKey(name)
    }

    suspend fun <T> set(key: Preferences.Key<T>, value: T) {
        dataStore.edit { prefs -> prefs[key] = value }
    }

    fun <T> get(key: Preferences.Key<T>): Flow<T?> =
        dataStore.data.map { prefs -> prefs[key] }
}