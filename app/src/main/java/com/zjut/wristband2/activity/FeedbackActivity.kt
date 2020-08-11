package com.zjut.wristband2.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zjut.wristband2.R
import com.zjut.wristband2.error.WCode
import com.zjut.wristband2.task.FeedbackTask
import com.zjut.wristband2.task.TaskListener
import com.zjut.wristband2.util.toast
import kotlinx.android.synthetic.main.activity_feedback.*

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class FeedbackActivity : AppCompatActivity() {

    private var type = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        setSupportActionBar(toolbar)
        with(toolbar) {
            navigationIcon = getDrawable(R.drawable.ic_menu_back)
            setNavigationOnClickListener {
                finish()
            }
        }
        radioGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.radioButton0 -> type = 0
                R.id.radioButton1 -> type = 1
                R.id.radioButton2 -> type = 2
            }
        }
        contentText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                commit.isEnabled = !TextUtils.isEmpty(p0)
            }
        })
        commit.setOnClickListener {
            val typeName = when (type) {
                0 -> "功能异常"
                1 -> "优化建议"
                else -> "其他反馈"
            }
            val content = contentText.text.toString()
            var contact = contactText.text.toString()
            if (TextUtils.isEmpty(contact)) {
                contact = "未知"
            }
            FeedbackTask(object : TaskListener {
                override fun onStart() {
                    progressBar.visibility = View.VISIBLE
                }

                override fun onSuccess() {
                    progressBar.visibility = View.GONE
                    toast(this@FeedbackActivity, "上传成功！")
                }

                override fun onFail(code: WCode) {
                    progressBar.visibility = View.GONE
                    toast(this@FeedbackActivity, code.error)
                }

            }).execute(typeName, content, contact)
        }
    }
}
