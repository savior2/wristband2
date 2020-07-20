package com.zjut.wristband2.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zjut.wristband2.R
import kotlinx.android.synthetic.main.activity_daily_sports_indoor.*

class DailySportsIndoorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_sports_indoor)
        setSupportActionBar(toolbar)
        with(toolbar) {
            navigationIcon = getDrawable(R.drawable.ic_menu_back)
            setNavigationOnClickListener {
                finish()
            }
        }
    }
}