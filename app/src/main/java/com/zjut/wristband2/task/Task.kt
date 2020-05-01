package com.zjut.wristband2.task

import android.os.AsyncTask
import android.util.Log
import com.google.gson.Gson
import com.zjut.wristband2.MyApplication
import com.zjut.wristband2.error.WCode
import com.zjut.wristband2.repo.*
import com.zjut.wristband2.util.SpUtil
import com.zjut.wristband2.util.TimeTransfer
import com.zjut.wristband2.util.WebUtil
import java.lang.StringBuilder
import java.util.*

class LoginTask(
    private val listener: TaskListener
) : BasicTask(listener) {
    override fun doInBackground(vararg p0: String) = WebUtil.login(p0[0], p0[1])
}

class VerifyCodeTask(
    private val listener: TaskListener
) : BasicTask(listener) {
    override fun doInBackground(vararg p0: String) = WebUtil.getVerifyCode(p0[0])
}


class ResetPasswordTask(
    private val listener: TaskListener
) : BasicTask(listener) {
    override fun doInBackground(vararg p0: String) =
        WebUtil.resetPassword(p0[0], p0[1], p0[2])
}

class ModifyPasswordTask(
    private val listener: TaskListener
) : BasicTask(listener) {
    override fun doInBackground(vararg p0: String) =
        WebUtil.modifyPassword(p0[0], p0[1], p0[2])
}


class FeedbackTask(
    private val listener: TaskListener
) : BasicTask(listener) {
    override fun doInBackground(vararg p0: String): WCode {
        with(SpUtil.getSp(SpUtil.SpAccount.FILE_NAME)) {
            val sid = getString(SpUtil.SpAccount.SID, "")!!
            val name = getString(SpUtil.SpAccount.NAME, "")!!
            return WebUtil.feedback(sid, name, p0[0], p0[1], p0[2])
        }
    }
}


class DeviceConnectTask(
    private val listener: TaskListener
) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg p0: Void?): Void? = null
    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        listener.onSuccess()
    }
}


class DailyHeartTask(
    private val listener: DailyHeartListener
) : AsyncTask<Date, Void, List<DailyHeart>>() {
    override fun doInBackground(vararg p0: Date): List<DailyHeart> {
        val start = TimeTransfer.date2Utc(p0[0])
        val end = start + 86400
        val device =
            SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getString(SpUtil.SpAccount.MAC_ADDRESS, "")
        return MyDatabase.instance.getDailyHeartDao().findByUtcAndDevice(start, end, device!!)
    }

    override fun onPostExecute(result: List<DailyHeart>) {
        listener.onSuccess(result)
    }
}

class AerobicsSummaryTask : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg p0: Void?): Void? {
        with(SpUtil.getSp(SpUtil.SpAccount.FILE_NAME)) {
            val id = MyDatabase.instance.getAerobicsSummaryDao().insert(
                AerobicsSummary(
                    getString(SpUtil.SpAccount.SID, "")!!,
                    getString(SpUtil.SpAccount.MAC_ADDRESS, "")!!,
                    TimeTransfer.nowUtcMillion()
                )
            )
            MyApplication.num = id
        }
        return null
    }
}

class PostAerobicsTask(
    private val listener: TaskListener
) : AsyncTask<AerobicsPosition, Void, WCode>() {
    override fun onPostExecute(result: WCode) {
        super.onPostExecute(result)
        if (result == WCode.OK) {
            listener.onSuccess()
        } else {
            listener.onFail(result)
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        listener.onStart()
    }

    override fun doInBackground(vararg p0: AerobicsPosition): WCode {
        MyDatabase.instance.getAerobicsPositionDao().insert(*p0)
        val summary = MyDatabase.instance.getAerobicsSummaryDao().findById(MyApplication.num)
        val hearts = MyDatabase.instance.getAerobicsHeartDao().findBySummaryId(MyApplication.num)
        with(SpUtil.getSp(SpUtil.SpAccount.FILE_NAME)) {
            val token = getString(SpUtil.SpAccount.TOKEN, "")!!
            val heartString = StringBuilder()
            val positionString = StringBuilder()
            val speedString = StringBuilder()
            for (i in hearts) {
                heartString.append("${i.utc},${i.rate},")
            }
            for (i in p0) {
                positionString.append("${i.utc},lng${i.longitude},lat${i.latitude},")
                speedString.append("${i.utc},${i.speed},")
            }
            val info = Gson().toJson(
                AerobicsJson(
                    token,
                    summary.sid,
                    summary.deviceId.replace(":", "").toLowerCase(),
                    summary.startUtc.toString(),
                    positionString.toString(),
                    speedString.toString(),
                    heartString.toString()
                )
            )
            return WebUtil.postAerobics(info)
        }
    }
}

class SportsSummaryTask : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg p0: Void?): Void? {
        with(SpUtil.getSp(SpUtil.SpAccount.FILE_NAME)) {
            val id = MyDatabase.instance.getSportsSummaryDao().insert(
                SportsSummary().apply {
                    sid = getString(SpUtil.SpAccount.SID, "")!!
                    deviceId = getString(SpUtil.SpAccount.MAC_ADDRESS, "")!!
                    startUtc = TimeTransfer.nowUtcMillion()
                }
            )
            MyApplication.num = id
            return null
        }
    }
}

class PostSportsRealTimeTask(
    private val listener: TaskListener
) : AsyncTask<SportsPosition, Void, WCode>() {
    override fun onPostExecute(result: WCode) {
        super.onPostExecute(result)
        if (result == WCode.OK) {
            listener.onSuccess()
        } else {
            listener.onFail(result)
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        listener.onStart()
    }


    override fun doInBackground(vararg p0: SportsPosition): WCode {
        MyDatabase.instance.getSportsPositionDao().insert(*p0)
        val summary = MyDatabase.instance.getSportsSummaryDao().findById(MyApplication.num)
        val hearts = MyDatabase.instance.getSportsHeartDao().findBySportsId(MyApplication.num)
        for (i in hearts.indices) {
            hearts[i].status = 1
        }
        MyDatabase.instance.getSportsHeartDao().update(*hearts.toTypedArray())
        with(SpUtil.getSp(SpUtil.SpAccount.FILE_NAME)) {
            val token = getString(SpUtil.SpAccount.TOKEN, "")!!
            val count = minOf(p0.size, hearts.size)
            val details = arrayListOf<SportsDetailJson>()
            for (i in 0 until count) {
                details.add(
                    SportsDetailJson(
                        time = p0[i].utc.toString(),
                        heartRate = hearts[i].rate.toString(),
                        position = "lng${p0[i].longitude},lat${p0[i].latitude}"
                    )
                )
            }
            val info = SportsRealtimeJson(
                token = token,
                studentId = summary.sid,
                deviceId = summary.deviceId.replace(":", "").toLowerCase(),
                mode = "running",
                detail = details
            )
            Log.e("test", Gson().toJson(info))
            return WebUtil.postNormalSports(Gson().toJson(info))
        }
    }
}

class PostSportsFinalTask(
    private val listener: TaskListener
) : AsyncTask<String, Void, WCode>() {
    //运功时间、运动距离
    override fun doInBackground(vararg p0: String): WCode {
        val summary = MyDatabase.instance.getSportsSummaryDao().findById(MyApplication.num)
        summary.apply {
            exerciseTime = p0[0].toLong()
            distance = p0[1].toFloat()
        }
    }
}

