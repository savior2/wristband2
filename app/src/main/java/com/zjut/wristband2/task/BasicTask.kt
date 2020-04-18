package com.zjut.wristband2.task

import android.os.AsyncTask
import com.zjut.wristband2.error.WCode

abstract class BasicTask(private val listener: TaskListener) : AsyncTask<String, Void, WCode>() {
    override fun onPreExecute() {
        super.onPreExecute()
        listener.onStart()
    }

    override fun onPostExecute(result: WCode) {
        super.onPostExecute(result)
        if (result == WCode.OK) {
            listener.onSuccess()
        } else {
            listener.onFail(result)
        }
    }
}

interface TaskListener {
    fun onStart()
    fun onSuccess()
    fun onFail(code: WCode)
}