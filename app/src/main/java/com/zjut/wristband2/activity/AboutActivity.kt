package com.zjut.wristband2.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zjut.wristband2.R
import kotlinx.android.synthetic.main.activity_about.*


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