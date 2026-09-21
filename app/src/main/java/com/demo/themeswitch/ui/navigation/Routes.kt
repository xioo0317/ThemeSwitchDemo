package com.demo.themeswitch.ui.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import top.yukonga.miuix.kmp.nav.core.NavKey

/**
 * 类型安全的导航键，基于 miuix-nav（对齐 KernelSU manager 的写法）。
 *
 * miuix-nav 的 rememberNavBackStack 内部使用 kotlinx.serialization 的
 * KSerializer + JSON 保存/恢复返回栈，因此每个目的地必须标注 @Serializable；
 * 同时实现 Parcelable 并加 @Parcelize 以兼容其它需要 Parcelable 的场景。
 */
@Serializable
sealed interface Route : NavKey, Parcelable {
    @Parcelize
    @Serializable
    data object Main : Route

    @Parcelize
    @Serializable
    data object Appearance : Route

    @Parcelize
    @Serializable
    data object About : Route
}
