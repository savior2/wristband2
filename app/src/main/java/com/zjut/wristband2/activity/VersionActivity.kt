package com.zjut.wristband2.activity

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.zjut.wristband2.R
import kotlinx.android.synthetic.main.activity_version.*

class VersionActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_version)
        confirm.setOnClickListener {
            finish()
        }
    }
}
