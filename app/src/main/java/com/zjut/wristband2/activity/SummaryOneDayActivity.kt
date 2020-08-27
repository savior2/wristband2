package com.zjut.wristband2.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.zjut.wristband2.R
import com.zjut.wristband2.adapter.SportsOneDayAdapter
import com.zjut.wristband2.repo.SportsSummary
import com.zjut.wristband2.task.SummaryOneDayTask
import com.zjut.wristband2.task.SummaryOneDayTaskListener
import com.zjut.wristband2.util.TimeTransfer
import kotlinx.android.synthetic.main.activity_summary_one_day.*
import java.text.SimpleDateFormat

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class SummaryOneDayActivity : AppCompatActivity() {
    private var startUtc = 0L
    @SuppressLint("SimpleDateFormat")
    private val df = SimpleDateFormat("yyyy-MM-dd 运动情况")

    private lateinit var adapter2: SportsOneDayAdapter

    private val array = arrayListOf<SportsSummary>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary_one_day)
        startUtc = intent.getLongExtra(START_UTC, 0L)
        adapter2 = SportsOneDayAdapter(array, this)
        with(toolbar) {
            navigationIcon = getDrawable(R.drawable.ic_menu_back)
            setNavigationOnClickListener {
                finish()
            }
            title = df.format(TimeTransfer.utcMillion2Date(startUtc))
        }
        recyclerView.apply {
            val manager = LinearLayoutManager(this@SummaryOneDayActivity)
            manager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = manager
            addItemDecoration(
                DividerItemDecoration(
                    this@SummaryOneDayActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = adapter2
        }
        SummaryOneDayTask(object : SummaryOneDayTaskListener {
            override fun onSuccess(p: List<SportsSummary>) {
                for (i in p) {
                    array.add(i)
                }
                adapter2.notifyDataSetChanged()
            }

        }).execute(startUtc, startUtc + 86400000)
    }

    companion object {
        private const val START_UTC = "start_utc"
        fun getIntent(context: Context, startUtc: Long) =
            Intent(context, SummaryOneDayActivity::class.java).apply {
                putExtra(START_UTC, startUtc)
            }
    }
}
