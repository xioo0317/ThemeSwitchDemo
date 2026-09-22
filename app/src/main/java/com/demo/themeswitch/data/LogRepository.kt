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
 * 即 context.filesDir/log.txt。App 端只负责读取并展示，不做任何写入或清空，
 * 文件不存在时日志页展示空状态。后端只需向该路径逐行追加内容（每行一条，以 \n 结尾）。
 */
object LogRepository {
    private const val LOG_FILE_NAME = "log.txt"

    /** 日志文件（后端写入目标，App 只读），文件可能尚不存在 */
    fun getLogFile(context: Context): File = File(context.filesDir, LOG_FILE_NAME)

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
