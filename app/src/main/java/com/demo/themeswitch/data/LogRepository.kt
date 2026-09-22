package com.demo.themeswitch.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 日志读取仓库（前端只读）。
 *
 * 日志由后端直接写入应用私有目录：
 *   /data/user/0/com.demo.themeswitch/files/log.txt
 * 即 context.filesDir/log.txt。App 只保证文件存在并负责读取展示，不写入任何日志内容，
 * 也不清空；后端只需向该路径逐行追加（每行一条，以 \n 结尾）。
 */
object LogRepository {
    private const val LOG_FILE_NAME = "log.txt"

    /** 日志文件（后端写入目标） */
    fun getLogFile(context: Context): File = File(context.filesDir, LOG_FILE_NAME)

    /** 启动时确保空日志文件存在，后端可直接 append；已存在则不改动其内容 */
    suspend fun ensureLogFileExists(context: Context) = withContext(Dispatchers.IO) {
        val file = getLogFile(context)
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            file.createNewFile()
        }
    }

    /** 读取日志全部行；文件不存在或为空时返回空列表 */
    suspend fun readLogLines(context: Context): List<String> = withContext(Dispatchers.IO) {
        val file = getLogFile(context)
        if (!file.exists() || file.length() == 0L) {
            emptyList()
        } else {
            file.readLines()
        }
    }
}
