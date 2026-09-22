package com.demo.themeswitch.data

import kotlinx.serialization.Serializable

/**
 * 应用 / 后端共享配置（序列化为 config.json）。
 *
 * @param backendUrl 后端监听地址，默认 http://127.0.0.1:8080。
 *        App 端写入该值；C++ 后端启动时读取同一文件，据此确定监听地址与端口。
 */
@Serializable
data class AppConfig(
    val backendUrl: String = DEFAULT_BACKEND_URL,
) {
    companion object {
        const val DEFAULT_BACKEND_URL = "http://127.0.0.1:8080"
    }
}
