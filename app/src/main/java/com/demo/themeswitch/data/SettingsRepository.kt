package com.demo.themeswitch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
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
        val ENABLE_MONET = booleanPreferencesKey("enable_monet")
        val KEY_COLOR = intPreferencesKey("key_color")
        val COLOR_STYLE = stringPreferencesKey("color_style")
        val COLOR_SPEC = stringPreferencesKey("color_spec")
        val ENABLE_BLUR = booleanPreferencesKey("enable_blur")
        val ENABLE_FLOATING_BOTTOM_BAR = booleanPreferencesKey("enable_floating_bottom_bar")
        val ENABLE_FLOATING_BOTTOM_BAR_BLUR = booleanPreferencesKey("enable_floating_bottom_bar_blur")
        val ENABLE_SCROLL_ANIMATION = booleanPreferencesKey("enable_scroll_animation")
        val ENABLE_PREDICTIVE_BACK = booleanPreferencesKey("enable_predictive_back")
        val PAGE_SCALE = floatPreferencesKey("page_scale")
        val SERVER_URL = stringPreferencesKey("server_url")
    }

    val preferencesFlow: Flow<AppPreferences> = context.dataStore.data.map { prefs ->
        AppPreferences(
            themeMode = prefs[PreferencesKeys.THEME_MODE] ?: 0,
            uiMode = prefs[PreferencesKeys.UI_MODE] ?: SettingsRepository.UI_MODE_MATERIAL,
            language = prefs[PreferencesKeys.LANGUAGE] ?: "system",
            enableMonet = prefs[PreferencesKeys.ENABLE_MONET] ?: true,
            keyColor = prefs[PreferencesKeys.KEY_COLOR] ?: 0,
            colorStyle = prefs[PreferencesKeys.COLOR_STYLE] ?: "TonalSpot",
            colorSpec = prefs[PreferencesKeys.COLOR_SPEC] ?: "SPEC_2021",
            enableBlur = prefs[PreferencesKeys.ENABLE_BLUR] ?: true,
            enableFloatingBottomBar = prefs[PreferencesKeys.ENABLE_FLOATING_BOTTOM_BAR] ?: false,
            enableFloatingBottomBarBlur = prefs[PreferencesKeys.ENABLE_FLOATING_BOTTOM_BAR_BLUR] ?: false,
            enableScrollAnimation = prefs[PreferencesKeys.ENABLE_SCROLL_ANIMATION] ?: true,
            enablePredictiveBack = prefs[PreferencesKeys.ENABLE_PREDICTIVE_BACK] ?: true,
            pageScale = prefs[PreferencesKeys.PAGE_SCALE] ?: 1.0f,
            serverUrl = prefs[PreferencesKeys.SERVER_URL] ?: "http://127.0.0.1:8080",
        )
    }

    suspend fun setThemeMode(mode: Int) {
        context.dataStore.edit { it[PreferencesKeys.THEME_MODE] = mode }
    }

    suspend fun setUiMode(mode: String) {
        context.dataStore.edit { it[PreferencesKeys.UI_MODE] = mode }
    }

    suspend fun setLanguage(language: String) {
        LocaleHelper.persistLanguage(context, language)
        context.dataStore.edit { it[PreferencesKeys.LANGUAGE] = language }
    }

    suspend fun setEnableMonet(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ENABLE_MONET] = enabled }
    }

    suspend fun setKeyColor(color: Int) {
        context.dataStore.edit { it[PreferencesKeys.KEY_COLOR] = color }
    }

    suspend fun setColorStyle(style: String) {
        context.dataStore.edit { it[PreferencesKeys.COLOR_STYLE] = style }
    }

    suspend fun setColorSpec(spec: String) {
        context.dataStore.edit { it[PreferencesKeys.COLOR_SPEC] = spec }
    }

    suspend fun setEnableBlur(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ENABLE_BLUR] = enabled }
    }

    suspend fun setEnableFloatingBottomBar(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ENABLE_FLOATING_BOTTOM_BAR] = enabled }
    }

    suspend fun setEnableFloatingBottomBarBlur(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ENABLE_FLOATING_BOTTOM_BAR_BLUR] = enabled }
    }

    suspend fun setEnableScrollAnimation(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ENABLE_SCROLL_ANIMATION] = enabled }
    }

    suspend fun setEnablePredictiveBack(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ENABLE_PREDICTIVE_BACK] = enabled }
    }

    suspend fun setPageScale(scale: Float) {
        context.dataStore.edit { it[PreferencesKeys.PAGE_SCALE] = scale }
    }

    suspend fun setServerUrl(url: String) {
        context.dataStore.edit { it[PreferencesKeys.SERVER_URL] = url }
    }

    companion object {
        const val UI_MODE_MATERIAL = "material"
        const val UI_MODE_MIUIX = "miuix"
    }
}
