package com.zjut.wristband2.task

import android.os.AsyncTask
import android.os.Environment
import com.google.gson.Gson
import com.zjut.wristband2.MyApplication
import com.zjut.wristband2.error.WCode
import com.zjut.wristband2.repo.*
import com.zjut.wristband2.util.SpUtil
import com.zjut.wristband2.util.TimeTransfer
import com.zjut.wristband2.util.WebUtil
import com.zjut.wristband2.util.baiduToGaode
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile
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
    private val listener: SimpleTaskListener<List<DailyHeart>>
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

class SportsSummaryTask(private val listener: SimpleTaskListener<Unit>?) :
    AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg p0: Void?): Void? {
        with(SpUtil.getSp(SpUtil.SpAccount.FILE_NAME)) {
            val id = MyDatabase.instance.getSportsSummaryDao().insert(
                SportsSummary().apply {
                    sid = getString(SpUtil.SpAccount.SID, "")!!
                    deviceId = getString(SpUtil.SpAccount.MAC_ADDRESS, "")!!
                    startUtc = TimeTransfer.nowUtcMillion()
                    mode = MyApplication.mode.mode
                }
            )
            MyApplication.num = id
            return null
        }
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        listener?.onSuccess(Unit)
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
        if (p0.isNotEmpty()) {
            MyDatabase.instance.getSportsPositionDao().insert(*p0)
            val summary = MyDatabase.instance.getSportsSummaryDao().findById(MyApplication.num)
            val hearts =
                MyDatabase.instance.getSportsHeartDao().findBySportsIdAndStatus(MyApplication.num)
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
                    mode = MyApplication.mode.mode,
                    detail = details
                )

                val tt = arrayListOf<Position>()
                for (i in p0) {
                    val cc = baiduToGaode(i.longitude.toDouble(), i.latitude.toDouble())
                    tt.add(
                        Position(
                            name = summary.sid,
                            longitude = cc.longitude,
                            latitude = cc.latitude,
                            updateTime = i.utc.toString()
                        )
                    )
                }
                WebUtil.temp(tt)
                return WebUtil.postNormalSports(Gson().toJson(info))
            }
        } else {
            val summary = MyDatabase.instance.getSportsSummaryDao().findById(MyApplication.num)
            val hearts =
                MyDatabase.instance.getSportsHeartDao().findBySportsIdAndStatus(MyApplication.num)
            for (i in hearts.indices) {
                hearts[i].status = 1
            }
            MyDatabase.instance.getSportsHeartDao().update(*hearts.toTypedArray())
            with(SpUtil.getSp(SpUtil.SpAccount.FILE_NAME)) {
                val token = getString(SpUtil.SpAccount.TOKEN, "")!!
                val count = hearts.size
                val details = arrayListOf<SportsDetailJson>()
                for (i in 0 until count) {
                    details.add(
                        SportsDetailJson(
                            time = hearts[i].utc.toString(),
                            heartRate = hearts[i].rate.toString(),
                            position = ""
                        )
                    )
                }
                val info = SportsRealtimeJson(
                    token = token,
                    studentId = summary.sid,
                    deviceId = summary.deviceId.replace(":", "").toLowerCase(),
                    mode = summary.mode ?: "",
                    detail = details
                )
                return WebUtil.postNormalSports(Gson().toJson(info))
            }
        }
    }
}

class PostSportsFinalTask(
    private val listener: TaskListener
) : AsyncTask<String, Void, WCode>() {

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

    //运功时间、运动距离
    override fun doInBackground(vararg p0: String): WCode {
        val summary = MyDatabase.instance.getSportsSummaryDao().findById(MyApplication.num)
        val hearts = MyDatabase.instance.getSportsHeartDao().findBySportsId(MyApplication.num)
        summary.apply {
            exerciseTime = p0[0].toLong()
            var d = 0F
            if (p0.size > 1) {
                d = p0[1].toFloat()
            }
            distance = d
            var sum = 0
            if (hearts.isNotEmpty()) {
                summary.maxHeartRate = hearts[0].rate
                for (i in hearts) {
                    sum += i.rate
                }
                sum /= hearts.size
            }
            avgHeartRate = sum
        }
        MyDatabase.instance.getSportsSummaryDao().update(summary)
        val token =
            SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getString(SpUtil.SpAccount.TOKEN, "")!!
        val info = SportsSummaryJson(
            token = token,
            studentId = summary.sid,
            deviceId = summary.deviceId.replace(":", "").toLowerCase(),
            mode = summary.mode ?: "",
            startTime = summary.startUtc.toString(),
            exerciseTime = summary.exerciseTime.toString(),
            distance = summary.distance.toString(),
            maxHeartRate = summary.maxHeartRate.toString(),
            avgHeartRate = summary.avgHeartRate.toString()
        )
        return WebUtil.postNormalSports(Gson().toJson(info))
    }
}


class PickDateTask(
    private val listener: PickDateTaskListener
) : AsyncTask<Long, Void, Boolean>() {
    override fun doInBackground(vararg p0: Long?): Boolean {
        val s = MyDatabase.instance.getSportsSummaryDao()
            .findByTime(p0[0]!!, p0[1]!!)
        return s.isEmpty()
    }

    override fun onPostExecute(result: Boolean) {
        super.onPostExecute(result)
        listener.onSuccess(result)
    }
}


class SummaryOneDayTask(
    private val listener: SummaryOneDayTaskListener
) : AsyncTask<Long, Void, List<SportsSummary>>() {
    override fun doInBackground(vararg p0: Long?) = MyDatabase.instance.getSportsSummaryDao()
        .findByTime(p0[0]!!, p0[1]!!)

    override fun onPostExecute(result: List<SportsSummary>) {
        super.onPostExecute(result)
        listener.onSuccess(result)
    }
}


class SportsPositionTask(
    private val listener: SportsPositionTaskListener
) : AsyncTask<Long, Void, List<SportsPosition>>() {
    override fun doInBackground(vararg p0: Long?) =
        MyDatabase.instance.getSportsPositionDao().findBySportsId(p0[0]!!)

    override fun onPostExecute(result: List<SportsPosition>) {
        super.onPostExecute(result)
        listener.onSuccess(result)
    }
}

class SportsPositionTask2(
    private val listener: SportsPositionTask2Listener
) : AsyncTask<Long, Void, SportsSummary>() {
    override fun doInBackground(vararg p0: Long?) = MyDatabase.instance.getSportsSummaryDao()
        .findById(p0[0]!!)

    override fun onPostExecute(result: SportsSummary) {
        super.onPostExecute(result)
        listener.onSuccess(result)
    }
}


class SportsHeartTask(
    private val listener: SportsHeartListener
) : AsyncTask<Long, Void, List<SportsHeart>>() {
    override fun doInBackground(vararg p0: Long?): List<SportsHeart> {
        val array = arrayListOf<SportsHeart>()
        for (i in p0) {
            array.addAll(MyDatabase.instance.getSportsHeartDao().findBySportsId2(i!!))
        }
        return array
    }


    override fun onPostExecute(result: List<SportsHeart>) {
        super.onPostExecute(result)
        listener.onSuccess(result)
    }
}


class VersionTask(
    private val listener: SimpleTaskListener<Version?>
) : AsyncTask<Void, Void, Version?>() {
    override fun doInBackground(vararg params: Void?): Version? = WebUtil.getVersion()

    override fun onPostExecute(result: Version?) = listener.onSuccess(result)
}


class DownloadTask(private val listener: DownloadListener) : AsyncTask<String, Int, Int>() {

    private var last = 0
    override fun doInBackground(vararg params: String?): Int {
        var input: InputStream? = null
        val path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
        val file = File(path, "wristband.apk")
        if (file.exists()) {
            file.delete()
        }
        val saved = RandomAccessFile(file, "rw")
        val url = params[0]!!
        val contentLength = getContentLength(url)
        val request = Request.Builder()
            .url(url)
            .build()
        val response = OkHttpClient()
            .newCall(request)
            .execute()
        try {
            input = response.body!!.byteStream()
            val byte = ByteArray(1024)
            var total = 0
            var len = 0
            while (true) {
                len = input.read(byte)
                if (len == -1) {
                    break
                }
                total += len
                saved.write(byte, 0, len)
                val p = (total * 100 / contentLength).toInt()
                publishProgress(p)
            }
            return 0
        } catch (e: Exception) {
            return 1
        } finally {
            input?.close()
            saved.close()
            response.close()
        }
    }

    override fun onPostExecute(result: Int) {
        super.onPostExecute(result)
        when (result) {
            0 -> listener.onSuccess()
            1 -> listener.onFail()
        }
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        val v = values[0]!!
        if (v > last) {
            listener.onProgress(v)
            last = v
        }
    }

    private fun getContentLength(url: String): Long {
        val request = Request.Builder()
            .url(url)
            .build()
        val response = OkHttpClient()
            .newCall(request)
            .execute()
        val length = response.body?.contentLength() ?: 0
        response.close()
        return length
    }
}



