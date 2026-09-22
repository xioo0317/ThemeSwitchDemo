package com.demo.themeswitch.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import java.io.File

/**
 * 基于 JSON 文件的配置仓库。
 *
 * 文件位置：App 私有 files 目录
 *   /data/data/com.demo.themeswitch/files/config.json
 *
 * 该文件同时供 C++ 本地后端读取：后端以 root 运行时可直接访问上述路径，
 * 启动时解析其中的 backendUrl（host/port），确定监听地址。
 */
class ConfigRepository(context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    private val configFile: File = File(context.filesDir, FILE_NAME)
    private val writeLock = Mutex()

    private val _config = MutableStateFlow(load())
    val configFlow: StateFlow<AppConfig> = _config.asStateFlow()

    /** 读取磁盘配置；文件不存在时用默认值初始化并落盘，保证后端首次启动即可读到。 */
    private fun load(): AppConfig = try {
        if (configFile.exists()) {
            json.decodeFromString(AppConfig.serializer(), configFile.readText())
        } else {
            AppConfig().also { persist(it) }
        }
    } catch (e: Exception) {
        // 文件损坏 / 解析失败时回退默认配置，避免首页白屏
        AppConfig()
    }

    private fun persist(config: AppConfig) {
        configFile.parentFile?.mkdirs()
        configFile.writeText(json.encodeToString(AppConfig.serializer(), config))
    }

    /** 更新后端地址；去除首尾空白与结尾斜杠后写盘。 */
    suspend fun setBackendUrl(url: String) = writeLock.withLock {
        val normalized = url.trim().trimEnd('/')
        if (normalized.isEmpty() || normalized == _config.value.backendUrl) return@withLock
        val updated = _config.value.copy(backendUrl = normalized)
        persist(updated)
        _config.value = updated
    }

    companion object {
        const val FILE_NAME = "config.json"
    }
}
