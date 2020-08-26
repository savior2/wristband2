package com.zjut.wristband2.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.zjut.wristband2.R
import com.zjut.wristband2.error.WCode
import com.zjut.wristband2.task.LoginTask
import com.zjut.wristband2.task.TaskListener
import com.zjut.wristband2.util.SpUtil
import com.zjut.wristband2.util.toast
import kotlinx.android.synthetic.main.activity_login.*

/**
 * @author qpf
 * @date 2020-8
 * @description 浙工健行的登录界面
 */
/**
 * onCreate里主要写一些监听事件*/
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //状态以key-value的形式存储在savedInstanceState中，可以下次启动acitivity时使用结束前的状态，但这个项目中未使用到
        //为了确保能够绘制界面
        super.onCreate(savedInstanceState)
        //将activity_login.xml的布局文件加载到该activity中
        setContentView(R.layout.activity_login)
        with(SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getString(SpUtil.SpAccount.SID, "")) {
            //SpUtil是方法类，里面存取得键值，获取到SID得
            if (!TextUtils.isEmpty(this)) {
                sidEditText.setText(this)
                //设置光标在最后，文本区域全选，使用得前提输入不为空，反则可能存在闪退
                sidEditText.setSelection(this!!.length)
            }
        }
        forgetPasswdButton.setOnClickListener {
            //打开新的activity
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
                        //加载图标的显示
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
        //隐藏键盘
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(loginButton.windowToken, 0)
    }


    /**
     * 可以后续打印日志使用，目前未使用到，如果后面使用可类名.方法名（）进行使用，一个类的伴生对象只能存在一个
     */
    companion object {
        private const val TAG = "LoginActivity"
    }
}
