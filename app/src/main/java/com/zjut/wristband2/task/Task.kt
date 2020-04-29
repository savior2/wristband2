package com.zjut.wristband2.task

import android.os.AsyncTask
import com.zjut.wristband2.error.WCode
import com.zjut.wristband2.repo.DailyHeart
import com.zjut.wristband2.repo.MyDatabase
import com.zjut.wristband2.util.SpUtil
import com.zjut.wristband2.util.TimeTransfer
import com.zjut.wristband2.util.WebUtil
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

class PostAerobicsTask(
    private val listener: TaskListener
) : BasicTask(listener) {
    override fun doInBackground(vararg p0: kotlin.String): WCode = WebUtil.postAerobics(p0[0])
}

