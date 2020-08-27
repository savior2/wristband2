package com.zjut.wristband2.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zjut.wristband2.R
import kotlinx.android.synthetic.main.activity_daily_sports_outdoor.*

/**
 * @author qpf
 * @date 2020-8
 * @description no network
 */
class DailySportsOutdoorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_sports_outdoor)
        setSupportActionBar(toolbar)
        with(toolbar) {
            navigationIcon = getDrawable(R.drawable.ic_menu_back)
            setNavigationOnClickListener {
                finish()
            }
        }
    }
}