package com.demo.themeswitch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = intPreferencesKey("theme_mode")
        val UI_MODE = stringPreferencesKey("ui_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val KEY_COLOR = intPreferencesKey("key_color")
        val MIUIX_MONET = booleanPreferencesKey("miuix_monet")
    }

    val preferencesFlow: Flow<AppPreferences> = context.dataStore.data.map { prefs ->
        AppPreferences(
            themeMode = prefs[PreferencesKeys.THEME_MODE] ?: 0,
            uiMode = prefs[PreferencesKeys.UI_MODE] ?: SettingsRepository.UI_MODE_MATERIAL,
            language = prefs[PreferencesKeys.LANGUAGE] ?: "system",
            keyColor = prefs[PreferencesKeys.KEY_COLOR] ?: 0,
            isMiuixMonet = prefs[PreferencesKeys.MIUIX_MONET] ?: false,
        )
    }

    suspend fun setThemeMode(mode: Int) {
        context.dataStore.edit { it[PreferencesKeys.THEME_MODE] = mode }
    }

    suspend fun setUiMode(mode: String) {
        context.dataStore.edit { it[PreferencesKeys.UI_MODE] = mode }
    }

    suspend fun setKeyColor(color: Int) {
        context.dataStore.edit { it[PreferencesKeys.KEY_COLOR] = color }
    }

    suspend fun setMiuixMonet(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.MIUIX_MONET] = enabled }
    }

    suspend fun setLanguage(language: String) {
        LocaleHelper.persistLanguage(context, language)
        context.dataStore.edit { it[PreferencesKeys.LANGUAGE] = language }
    }

    companion object {
        const val UI_MODE_MATERIAL = "material"
        const val UI_MODE_MIUIX = "miuix"
    }
}
