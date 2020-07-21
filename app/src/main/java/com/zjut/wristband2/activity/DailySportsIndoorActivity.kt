package com.zjut.wristband2.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
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
        val array = arrayListOf("跑步机", "功率自行车", "举重", "羽毛球", "排球", "网球", "乒乓球", "跳绳")
        val adapter = ArrayAdapter(this, R.layout.module_spinner_item, array)
        adapter.setDropDownViewResource(R.layout.module_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.e("test", position.toString())
                Log.e("test", spinner.selectedItem.toString())
            }

        }
        //spinner.isEnabled = false

    }
}