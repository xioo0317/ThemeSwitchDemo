package com.demo.themeswitch.ui.navigation

import top.yukonga.miuix.kmp.nav.core.NavKey

/**
 * KSU 同款类型安全路由。
 * 用 miuix-nav 的 NavBackStack 管理回退栈，支持预测性手势返回转场动画。
 */
sealed interface Route : NavKey {
    data object Main : Route
    data object Appearance : Route
    data object About : Route
}
