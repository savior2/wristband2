package com.zjut.wristband2.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zjut.wristband2.R
import com.zjut.wristband2.error.WCode
import com.zjut.wristband2.task.ResetPasswordTask
import com.zjut.wristband2.task.TaskListener
import com.zjut.wristband2.task.VerifyCodeTask
import com.zjut.wristband2.util.toast
import kotlinx.android.synthetic.main.activity_forget_password.*

class ForgetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        setSupportActionBar(toolbar)
        toolbar.apply {
            navigationIcon = getDrawable(R.drawable.ic_menu_back)
            setNavigationOnClickListener {
                finish()
            }
        }
        getCodeButton.setOnClickListener {
            getVerifyCode()
        }
        resetButton.setOnClickListener {
            reset()
        }
    }

    private fun getVerifyCode() {
        val sid = sidText.text.toString()
        if (TextUtils.isEmpty(sid)) {
            toast(this, "请先输入学号！")
        } else {
            TimeCount(60000, 1000).start()
            VerifyCodeTask(object : TaskListener {
                override fun onStart() {
                }

                override fun onSuccess() {
                    toast(this@ForgetPasswordActivity, "验证码已发送！")
                }

                override fun onFail(code: WCode) {
                    toast(this@ForgetPasswordActivity, code.error)
                }

            }).execute(sid)
        }
    }

    private fun reset() {
        val sid = sidText.text.toString()
        val newPassword = newPasswordText.text.toString()
        val newPassword2 = newPassword2Text.text.toString()
        val verifyCode = verifyCodeText.text.toString()
        when {
            TextUtils.isEmpty(sid) || TextUtils.isEmpty(newPassword)
                    || TextUtils.isEmpty(newPassword2)
                    || TextUtils.isEmpty(verifyCode) ->
                toast(this, "参数均不能为空！")
            newPassword != newPassword2 ->
                toast(this, "两次密码不一致！")
            else -> {
                ResetPasswordTask(object : TaskListener {
                    override fun onStart() {
                        progressBar.visibility = View.VISIBLE
                    }

                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                        toast(this@ForgetPasswordActivity, "重置成功！")
                    }

                    override fun onFail(code: WCode) {
                        progressBar.visibility = View.GONE
                        toast(this@ForgetPasswordActivity, code.error)
                    }

                }).execute(sid, newPassword, verifyCode)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class TimeCount(
        millisInFuture: Long,
        countDownInterval: Long
    ) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            with(getCodeButton) {
                text = "获取验证码"
                isEnabled = true
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onTick(p0: Long) {
            with(getCodeButton) {
                text = "${p0 / 1000}秒后可重新获取"
                isEnabled = false
            }
        }
    }

    companion object {
        private const val TAG = "ForgetPasswordActivity"
    }
}
