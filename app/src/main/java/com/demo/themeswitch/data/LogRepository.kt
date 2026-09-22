package com.demo.themeswitch.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 日志仓库：从 app 私有 files 目录读取日志文件。
 * 日志文件路径：context.filesDir/log.txt
 * 该目录为应用私有，其他应用无法访问。
 */
object LogRepository {
    private const val LOG_FILE_NAME = "log.txt"

    /** 获取日志文件（app 私有目录下的 log.txt），文件可能不存在 */
    fun getLogFile(context: Context): File = File(context.filesDir, LOG_FILE_NAME)

    /** 读取日志所有行，按行返回；文件不存在或为空时返回空列表 */
    suspend fun readLogLines(context: Context): List<String> = withContext(Dispatchers.IO) {
        val file = getLogFile(context)
        if (!file.exists() || file.length() == 0L) {
            emptyList()
        } else {
            file.readLines().filter { it.isNotBlank() || true } // 保留空行结构，后续再过滤
        }
    }

    /** 追加一行日志到私有目录 log.txt（供后续服务端写入时使用） */
    suspend fun appendLog(context: Context, message: String) = withContext(Dispatchers.IO) {
        val file = getLogFile(context)
        file.appendText("$message\n")
    }

    /** 清空日志文件 */
    suspend fun clearLog(context: Context) = withContext(Dispatchers.IO) {
        val file = getLogFile(context)
        if (file.exists()) {
            file.writeText("")
        }
    }
}
