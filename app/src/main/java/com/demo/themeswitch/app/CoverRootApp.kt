package com.demo.themeswitch.app

import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.Build
import com.demo.themeswitch.data.AppPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.lsposed.hiddenapibypass.HiddenApiBypass

class CoverRootApp : Application() {

    companion object {
        lateinit var instance: CoverRootApp
            private set

        fun setEnableOnBackInvokedCallback(appInfo: ApplicationInfo, enable: Boolean) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return
            runCatching {
                HiddenApiBypass.addHiddenApiExemptions(
                    "Landroid/content/pm/ApplicationInfo;->setEnableOnBackInvokedCallback"
                )
                val method = ApplicationInfo::class.java.getDeclaredMethod(
                    "setEnableOnBackInvokedCallback", Boolean::class.javaPrimitiveType
                )
                method.isAccessible = true
                method.invoke(appInfo, enable)
            }
        }
    }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        instance = this

        // KSU 同款：启动时根据偏好设置初始化预测性返回手势
        appScope.launch {
            val enable = AppPreferences(applicationContext).enablePredictiveBack
            setEnableOnBackInvokedCallback(applicationInfo, enable)
        }
    }
}
