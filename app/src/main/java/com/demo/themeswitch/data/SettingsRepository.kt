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

    private object Keys {
        val COLOR_MODE = intPreferencesKey("color_mode")
        val THEME_MODE = intPreferencesKey("theme_mode") // 旧字段，迁移用
        val UI_MODE = stringPreferencesKey("ui_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val KEY_COLOR = intPreferencesKey("key_color")
        val COLOR_STYLE = stringPreferencesKey("color_style")
        val COLOR_SPEC = stringPreferencesKey("color_spec")
        val MIUIX_MONET = booleanPreferencesKey("miuix_monet")
        val ENABLE_BLUR = booleanPreferencesKey("enable_blur")
        val ENABLE_FLOATING_BOTTOM_BAR = booleanPreferencesKey("enable_floating_bottom_bar")
        val ENABLE_FLOATING_BOTTOM_BAR_BLUR = booleanPreferencesKey("enable_floating_bottom_bar_blur")
        val ENABLE_SCROLL_ANIMATION = booleanPreferencesKey("enable_scroll_animation")
        val ENABLE_PREDICTIVE_BACK = booleanPreferencesKey("enable_predictive_back")
        val ENABLE_SWIPE_DISMISS = booleanPreferencesKey("enable_swipe_dismiss")
        val PAGE_SCALE = floatPreferencesKey("page_scale")
    }

    val preferencesFlow: Flow<AppPreferences> = context.dataStore.data.map { p ->
        // color_mode 缺失时回退到旧 theme_mode
        val colorMode = p[Keys.COLOR_MODE] ?: p[Keys.THEME_MODE] ?: 0
        AppPreferences(
            colorMode = colorMode,
            uiMode = p[Keys.UI_MODE] ?: UI_MODE_MIUIX,
            language = p[Keys.LANGUAGE] ?: "system",
            keyColor = p[Keys.KEY_COLOR] ?: DEFAULT_KEY_COLOR,
            colorStyle = p[Keys.COLOR_STYLE] ?: "TonalSpot",
            colorSpec = p[Keys.COLOR_SPEC] ?: "SPEC_2025",
            miuixMonet = p[Keys.MIUIX_MONET] ?: false,
            enableBlur = p[Keys.ENABLE_BLUR] ?: false,
            enableFloatingBottomBar = p[Keys.ENABLE_FLOATING_BOTTOM_BAR] ?: false,
            enableFloatingBottomBarBlur = p[Keys.ENABLE_FLOATING_BOTTOM_BAR_BLUR] ?: false,
            enableScrollAnimation = p[Keys.ENABLE_SCROLL_ANIMATION] ?: false,
            enablePredictiveBack = p[Keys.ENABLE_PREDICTIVE_BACK] ?: true,
            enableSwipeDismiss = p[Keys.ENABLE_SWIPE_DISMISS] ?: true,
            pageScale = p[Keys.PAGE_SCALE] ?: 1.0f,
        )
    }

    suspend fun setColorMode(mode: Int) {
        context.dataStore.edit { it[Keys.COLOR_MODE] = mode }
    }

    suspend fun setUiMode(mode: String) {
        context.dataStore.edit { it[Keys.UI_MODE] = mode }
    }

    suspend fun setLanguage(language: String) {
        LocaleHelper.persistLanguage(context, language)
        context.dataStore.edit { it[Keys.LANGUAGE] = language }
    }

    suspend fun setKeyColor(color: Int) {
        context.dataStore.edit { it[Keys.KEY_COLOR] = color }
    }

    suspend fun setColorStyle(style: String) {
        context.dataStore.edit { it[Keys.COLOR_STYLE] = style }
    }

    suspend fun setColorSpec(spec: String) {
        context.dataStore.edit { it[Keys.COLOR_SPEC] = spec }
    }

    suspend fun setMiuixMonet(enabled: Boolean) {
        context.dataStore.edit { it[Keys.MIUIX_MONET] = enabled }
    }

    suspend fun setEnableBlur(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ENABLE_BLUR] = enabled }
    }

    suspend fun setEnableFloatingBottomBar(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ENABLE_FLOATING_BOTTOM_BAR] = enabled }
    }

    suspend fun setEnableFloatingBottomBarBlur(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ENABLE_FLOATING_BOTTOM_BAR_BLUR] = enabled }
    }

    suspend fun setEnableScrollAnimation(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ENABLE_SCROLL_ANIMATION] = enabled }
    }

    suspend fun setEnablePredictiveBack(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ENABLE_PREDICTIVE_BACK] = enabled }
    }

    suspend fun setEnableSwipeDismiss(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ENABLE_SWIPE_DISMISS] = enabled }
    }

    suspend fun setPageScale(scale: Float) {
        context.dataStore.edit { it[Keys.PAGE_SCALE] = scale }
    }

    companion object {
        const val UI_MODE_MATERIAL = "material"
        const val UI_MODE_MIUIX = "miuix"

        // 默认 0 → 走壁纸 Monet 取色（对齐 KSU）；非 0 用固定种子色（如品牌 Teal #009688）
        const val DEFAULT_KEY_COLOR = 0
    }
}
