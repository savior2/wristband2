package com.zjut.wristband2.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.zjut.wristband2.R
import com.zjut.wristband2.error.WCode
import com.zjut.wristband2.task.ModifyPasswordTask
import com.zjut.wristband2.task.TaskListener
import com.zjut.wristband2.util.SpUtil
import com.zjut.wristband2.util.toast
import kotlinx.android.synthetic.main.activity_modify_password.*

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class ModifyPasswordActivity : AppCompatActivity() {

    private lateinit var sid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_password)
        setSupportActionBar(toolbar)
        sid = SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getString(SpUtil.SpAccount.SID, "")!!
        toolbar.apply {
            navigationIcon = getDrawable(R.drawable.ic_menu_back)
            setNavigationOnClickListener {
                finish()
            }
        }
        modifyButton.setOnClickListener {
            modify()
        }
    }

    private fun modify() {
        val old = oldPassword.text.toString()
        val new = newPasswordText.text.toString()
        val new2 = newPassword2Text.text.toString()
        when {
            TextUtils.isEmpty(old) or TextUtils.isEmpty(new) or TextUtils.isEmpty(new2) -> toast(
                this,
                "以上参数均不能为空！"
            )
            new != new2 -> toast(this, "两次密码输入不一致！")
            else -> {
                ModifyPasswordTask(object : TaskListener {
                    override fun onStart() {
                        progressBar.visibility = View.VISIBLE
                    }

                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                        toast(this@ModifyPasswordActivity, "修改成功！")
                        val intent = Intent(this@ModifyPasswordActivity, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }

                    override fun onFail(code: WCode) {
                        progressBar.visibility = View.GONE
                        toast(this@ModifyPasswordActivity, code.error)
                    }

                }).execute(sid, old, new)
            }
        }

    }
}
