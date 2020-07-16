package com.zjut.wristband2.util

import android.annotation.SuppressLint
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.text.SimpleDateFormat
import java.util.*


object LogUtil {

    private val LOG_PATH =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
    private var LOG_NAME = "/debug.txt"
    private const val INFO = "info"
    private const val DEBUG = "debug"
    private const val WARN = "warn"
    private const val VERBOSE = "verbose"
    private const val ERROR = "error"


    fun i(content: String) {
        saveLog(INFO, content)
    }

    fun v(content: String) {
        saveLog(VERBOSE, content)
    }

    fun d(content: String) {
        saveLog(DEBUG, content)
    }

    fun w(content: String) {
        saveLog(WARN, content)
    }

    fun e(content: String) {
        saveLog(ERROR, content)
    }

    @SuppressLint("SimpleDateFormat")
    fun saveLog(type: String, content: String) {
        appendToFile(
            LOG_PATH + LOG_NAME, SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(
                Date(
                    System.currentTimeMillis()
                )
            ) + "(" + type + "):" + content + "\r\n"
        )
    }

    private fun appendToFile(filePath: String?, content: String?) {
        var ranFile: RandomAccessFile? = null
        try {
            val file = File(filePath)
            if (!file.exists()) {
                file.createNewFile()
            }
            ranFile = RandomAccessFile(file, "rw")
            val fileLength: Long = ranFile.length()
            ranFile.seek(fileLength)
            ranFile.writeBytes(content)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                ranFile?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}