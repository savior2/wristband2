package com.zjut.wristband2.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zjut.wristband2.R
import kotlinx.android.synthetic.main.activity_version.*

class VersionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_version)
        confirm.setOnClickListener {
            finish()
        }
    }
}
