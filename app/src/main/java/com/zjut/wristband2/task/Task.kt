package com.zjut.wristband2.task

import android.os.AsyncTask
import com.zjut.wristband2.util.WebUtil

class LoginTask(
    private val listener: TaskListener
) : BasicTask(listener) {
    override fun doInBackground(vararg p0: String?) = WebUtil.login(p0[0]!!, p0[1]!!)
}

class VerifyCodeTask(
    private val listener: TaskListener
) : BasicTask(listener) {
    override fun doInBackground(vararg p0: String?) = WebUtil.getVerifyCode(p0[0]!!)
}


class ResetPasswordTask(
    private val listener: TaskListener
) : BasicTask(listener) {
    override fun doInBackground(vararg p0: String?) =
        WebUtil.resetPassword(p0[0]!!, p0[1]!!, p0[2]!!)
}

class ModifyPasswordTask(
    private val listener: TaskListener
) : BasicTask(listener) {
    override fun doInBackground(vararg p0: String?) =
        WebUtil.modifyPassword(p0[0]!!, p0[1]!!, p0[2]!!)
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
