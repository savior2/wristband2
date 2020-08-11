package com.zjut.wristband2.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.zjut.wristband2.R
import com.zjut.wristband2.repo.MyDatabase
import com.zjut.wristband2.util.TimeTransfer
import kotlinx.android.synthetic.main.activity_test.*
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.logging.SimpleFormatter

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class TestActivity : AppCompatActivity() {
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        with(toolbar) {
            navigationIcon = getDrawable(R.drawable.ic_menu_back)
            setNavigationOnClickListener {
                finish()
            }
        }

        button2.setOnClickListener {
            val text = editTextTextMultiLine.text.toString()
            if (TextUtils.isEmpty(text)) {
                Thread {
                    val a = MyDatabase.instance.getSportsSummaryDao().findAll()
                    val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val s = StringBuilder()
                    s.append("id 开始时间 运动时间 距离 最大心率 平均心率 模式\n")
                    for (i in a) {
                        s.apply {
                            append(i.id.toString() + " ")
                            append(df.format(TimeTransfer.utcMillion2Date(i.startUtc)) + " ")
                            append(i.exerciseTime.toString() + " ")
                            append(i.distance.toString() + " ")
                            append(i.maxHeartRate.toString() + " ")
                            append(i.avgHeartRate.toString() + " ")
                            append(i.mode + " ")
                            append("\n")
                        }
                    }
                    runOnUiThread(Runnable {
                        textView18.text = s.toString()
                    })
                }.start()
            } else {
                val t = text.split(" ")
                when (t[0]) {
                    "1" -> {
                        Thread {
                            val a =
                                MyDatabase.instance.getSportsHeartDao()
                                    .findBySportsId2(t[1].toLong())
                            val s = StringBuilder()
                            for (i in a) {
                                s.apply {
                                    append((i.utc / 1000).toString() + " ")
                                    append(i.rate.toString() + " ")
                                    append(i.status.toString() + " ")
                                    append("\n")
                                }
                            }
                            s.append(a.size)
                            runOnUiThread(Runnable {
                                textView18.text = s.toString()
                            })
                        }.start()
                    }
                    "2" -> {
                        Thread {
                            val a =
                                MyDatabase.instance.getSportsPositionDao()
                                    .findBySportsId(t[1].toLong())
                            val s = StringBuilder()
                            for (i in a) {
                                s.apply {
                                    append((i.utc / 1000).toString() + " ")
                                    append("(${i.longitude}, ${i.latitude}) ")
                                    append("${i.speed} ")
                                    append(i.status.toString() + " ")
                                    append("\n")
                                }
                            }
                            s.append(a.size)
                            runOnUiThread(Runnable {
                                textView18.text = s.toString()
                            })
                        }.start()
                    }
                }
            }
        }
    }
}

