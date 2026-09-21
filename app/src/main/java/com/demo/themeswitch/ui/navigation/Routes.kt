package com.demo.themeswitch.ui.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import top.yukonga.miuix.kmp.nav.core.NavKey

/**
 * KSU 同款类型安全路由。
 * 用 miuix-nav 的 NavBackStack 管理回退栈，支持预测性手势返回转场动画。
 */
@Parcelize
sealed interface Route : NavKey, Parcelable {
    @Parcelize
    data object Main : Route

    @Parcelize
    data object Appearance : Route

    @Parcelize
    data object About : Route
}
