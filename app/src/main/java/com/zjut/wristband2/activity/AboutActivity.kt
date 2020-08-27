package com.zjut.wristband2.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zjut.wristband2.R
import kotlinx.android.synthetic.main.activity_about.*


/**
 * @author qpf
 * @date 2020-8
 * @description the basic information of the app
 * 右侧tab页滑出的关于浙工健行的内容
 */
class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setSupportActionBar(toolbar)
        with(toolbar) {
            navigationIcon = getDrawable(R.drawable.ic_menu_back)
            setNavigationOnClickListener {
                finish()
            }
        }
    }
}