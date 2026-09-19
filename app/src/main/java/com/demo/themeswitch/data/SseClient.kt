package com.demo.themeswitch.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * 极简 SSE POST 客户端（零第三方依赖）。
 * POST /api/v1/execute 后逐行读取响应流：
 *   data: {"message":"开始执行指令"}
 *   ...
 *   data: [DONE]
 */
object SseClient {

    private const val DONE = "[DONE]"
    private const val DATA_PREFIX = "data: "

    /**
     * 流式执行请求。
     *
     * @param baseUrl 例如 http://127.0.0.1:8080
     * @param body    完整 JSON 请求体（须含 action 字段）
     * @param onFrame 每收到一帧回调一次（载荷已去掉 "data: " 前缀，[DONE] 也会回调）
     * @return 流正常以 [DONE] 结束时 success；HTTP 错误 / 连接失败 / 流提前中断时 failure
     */
    suspend fun execute(
        baseUrl: String,
        body: String,
        onFrame: (String) -> Unit,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val url = URL("$baseUrl/api/v1/execute")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 5_000
                readTimeout = 15_000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "text/event-stream")
            }
            try {
                conn.outputStream.use { it.write(body.toByteArray()) }

                val code = conn.responseCode
                if (code !in 200..299) {
                    val err = conn.errorStream?.bufferedReader()?.readText().orEmpty().take(200)
                    throw IllegalStateException("HTTP $code $err")
                }

                var sawDone = false
                conn.inputStream.use { stream ->
                    val reader = BufferedReader(InputStreamReader(stream))
                    while (true) {
                        val line = reader.readLine() ?: break
                        if (!line.startsWith(DATA_PREFIX)) continue
                        val payload = line.removePrefix(DATA_PREFIX)
                        onFrame(payload)
                        if (payload == DONE) {
                            sawDone = true
                            break
                        }
                    }
                }
                if (!sawDone) throw IllegalStateException("stream ended without [DONE]")
            } finally {
                conn.disconnect()
            }
        }
    }
}
