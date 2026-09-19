package com.demo.themeswitch.data

import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/** 后端连通性探测结果 */
data class ProbeResult(
    val online: Boolean,
    val latencyMs: Long,
)

/**
 * C++ 本地后端探活：POST /api/v1/execute（空 body）。
 * 只要拿到 HTTP 响应（200~599）即视为在线——400 同样证明服务器活着并明确拒绝空请求；
 * 连接超时 / 连接拒绝 / DNS 失败等 IOException 才判离线。
 */
suspend fun probeBackend(serverUrl: String): ProbeResult = withContext(Dispatchers.IO) {
    val normalized = serverUrl.trim().trimEnd('/')
    val start = SystemClock.elapsedRealtime()
    var connection: HttpURLConnection? = null
    try {
        connection = (URL("$normalized/api/v1/execute").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 2500
            readTimeout = 2500
            setRequestProperty("Content-Type", "application/json")
            doOutput = true
        }
        connection.outputStream.use { it.write(ByteArray(0)) }
        val code = connection.responseCode
        ProbeResult(online = code in 200..599, latencyMs = SystemClock.elapsedRealtime() - start)
    } catch (e: IOException) {
        ProbeResult(online = false, latencyMs = SystemClock.elapsedRealtime() - start)
    } finally {
        connection?.disconnect()
    }
}

/** 全局共享的探测状态：主页负责探测刷新，底栏导航角标等处只读 */
object BackendMonitor {
    var lastResult by mutableStateOf<ProbeResult?>(null)
        private set
    var lastProbedUrl by mutableStateOf<String?>(null)
        private set

    suspend fun refresh(serverUrl: String): ProbeResult {
        val result = probeBackend(serverUrl)
        lastResult = result
        lastProbedUrl = serverUrl
        return result
    }
}
