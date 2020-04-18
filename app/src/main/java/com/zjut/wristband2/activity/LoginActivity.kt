package com.zjut.wristband2.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.zjut.wristband2.R
import com.zjut.wristband2.error.WCode
import com.zjut.wristband2.task.LoginTask
import com.zjut.wristband2.task.TaskListener
import com.zjut.wristband2.util.SpUtil
import com.zjut.wristband2.util.WidgetUtil.toast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        with(SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getString(SpUtil.SpAccount.SID, "")) {
            if (!TextUtils.isEmpty(this)) {
                sidEditText.setText(this)
                sidEditText.setSelection(this!!.length)
            }
        }
        forgetPasswdButton.setOnClickListener {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }
        loginButton.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val sid = sidEditText.text.toString()
        val password = passwordEditText.text.toString()
        when {
            (TextUtils.isEmpty(sid) || TextUtils.isEmpty(password)) -> toast(this, "学号或密码不能为空")
            sid.length > 14 -> toast(this, "学号格式错误")
            else -> {
                LoginTask(object : TaskListener {
                    override fun onStart() {
                        progressBar.visibility = View.VISIBLE
                    }

                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                        toast(this@LoginActivity, "登录成功")
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    }

                    override fun onFail(code: WCode) {
                        progressBar.visibility = View.GONE
                        toast(this@LoginActivity, code.error)
                    }
                }).execute(sid, password)
            }
        }
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(loginButton.windowToken, 0)
    }


    companion object {
        private const val TAG = "LoginActivity"
    }
}
