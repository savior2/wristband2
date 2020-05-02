package com.zjut.wristband2.task

import android.os.AsyncTask
import com.zjut.wristband2.error.WCode
import com.zjut.wristband2.repo.DailyHeart
import com.zjut.wristband2.repo.SportsHeart
import com.zjut.wristband2.repo.SportsPosition
import com.zjut.wristband2.repo.SportsSummary

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

interface SimpleTaskListener {
    fun onSuccess(list: List<DailyHeart>)
}

interface PickDateTaskListener {
    fun onSuccess(p: Boolean)
}

interface SummaryOneDayTaskListener {
    fun onSuccess(p: List<SportsSummary>)
}

interface SportsPositionTaskListener {
    fun onSuccess(p: List<SportsPosition>)
}

interface SportsPositionTask2Listener {
    fun onSuccess(p: SportsSummary)
}


interface SportsHeartListener {
    fun onSuccess(p: List<SportsHeart>)
}