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
 * C++ 本地后端探活：GET /。
 * 只要拿到 HTTP 响应（200~599）即视为在线——即使返回 404 也证明 HTTP 服务已监听；
 * 连接超时 / 连接拒绝 / DNS 失败等 IOException 才判离线。
 *
 * 响应流（POST /api/v1/execute、SSE）已移除，后续状态通过日志文件获取，
 * 因此探活不再依赖任何业务接口。
 */
suspend fun probeBackend(serverUrl: String): ProbeResult = withContext(Dispatchers.IO) {
    val normalized = serverUrl.trim().trimEnd('/')
    val start = SystemClock.elapsedRealtime()
    var connection: HttpURLConnection? = null
    try {
        connection = (URL(normalized).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 2500
            readTimeout = 2500
            instanceFollowRedirects = false
        }
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
