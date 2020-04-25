package com.zjut.wristband2.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zjut.wristband2.R
import kotlinx.android.synthetic.main.activity_daily_heart.*

class DailyHeartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_heart)
        with(toolbar) {
            navigationIcon = getDrawable(com.zjut.wristband2.R.drawable.ic_menu_back)
            setNavigationOnClickListener {
                finish()
            }
        }
    }
}
