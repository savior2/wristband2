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

/**
 * @author qpf
 * @date 2020-8
 * @description 登录界面忘记密码
 */
/**
 * toolbar设置的是忘记密码最上方的重置密码（标题栏）
 *setSupportActionBar需要放在setNavigationOnClickListener前面反则设置无效
 *toolbar.apply的使用是为了减少重复使用toolbar
 */
class ForgetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        setSupportActionBar(toolbar)
        /*toolbar.apply {
        //返回的箭头，R.drawable.name是为了防止受当前的主题影响
            navigationIcon = getDrawable(R.drawable.ic_menu_back)
            setNavigationOnClickListener {
                finish()
            }
        }*/
        toolbar.navigationIcon = getDrawable(R.drawable.ic_menu_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        //获取验证码，获取之前校验信息
        getCodeButton.setOnClickListener {
            getVerifyCode()
        }
        //重置密码按钮
        resetButton.setOnClickListener {
            reset()
        }
    }

    private fun getVerifyCode() {
        val sid = sidText.text.toString()
        if (TextUtils.isEmpty(sid)) {
            toast(this, "请先输入学号！")
        } else {
            //第一个参数为总时间，第二个参数为间隔时间
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


    /**
     * 继承CountDownTimer*/
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

        /**
         * 注解用途忽略字符串拼接的检查
         */
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
