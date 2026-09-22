package com.demo.themeswitch.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import org.lsposed.hiddenapibypass.HiddenApiBypass

/**
 * 立即开关预测性返回手势。
 * 原理：通过 HiddenApiBypass 调用 ApplicationInfo.setEnableOnBackInvokedCallback 隐藏 API，
 * 让系统立即刷新当前进程的预测性返回状态，无需重启应用。
 *
 * 参考 KernelSU 实现：KernelSUApplication.kt
 */
fun setPredictiveBackEnabled(context: Context, enabled: Boolean) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return
    runCatching {
        HiddenApiBypass.addHiddenApiExemptions(
            "Landroid/content/pm/ApplicationInfo;->setEnableOnBackInvokedCallback"
        )
        val appInfo = context.applicationInfo
        val method = ApplicationInfo::class.java.getDeclaredMethod(
            "setEnableOnBackInvokedCallback",
            Boolean::class.javaPrimitiveType
        )
        method.isAccessible = true
        method.invoke(appInfo, enabled)
    }
}
