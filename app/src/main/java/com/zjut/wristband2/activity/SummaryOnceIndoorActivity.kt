package com.zjut.wristband2.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.zjut.wristband2.R
import com.zjut.wristband2.repo.SportsHeart
import com.zjut.wristband2.repo.SportsSummary
import com.zjut.wristband2.task.SportsHeartListener
import com.zjut.wristband2.task.SportsHeartTask
import com.zjut.wristband2.task.SportsPositionTask2
import com.zjut.wristband2.task.SportsPositionTask2Listener
import com.zjut.wristband2.util.SpUtil
import com.zjut.wristband2.util.TimeTransfer
import kotlinx.android.synthetic.main.activity_summary_once_indoor.*
import java.text.SimpleDateFormat
import java.util.*

class SummaryOnceIndoorActivity : AppCompatActivity() {
    private var id = 0L

    private lateinit var myMarkerView: MyMarkerView
    private val array = arrayListOf<SportsHeart>()
    private val array2 = arrayListOf<Float>()

    private var mAge = 24
    private var h0 = 0
    private var h1 = 0
    private var h2 = 0
    private var h3 = 0
    private var h4 = 0

    @SuppressLint("SimpleDateFormat")
    private val df = SimpleDateFormat("HH:mm:ss")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary_once_indoor)
        setSupportActionBar(toolbar)
        with(toolbar) {
            navigationIcon = getDrawable(R.drawable.ic_menu_back)
            setNavigationOnClickListener {
                finish()
            }
        }
        id = intent.getLongExtra(SPORTS_ID, 0L)
        val ageUtc = SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getLong(SpUtil.SpAccount.BIRTHDAY, 0)
        val age = Date().year - TimeTransfer.utcMillion2Date(ageUtc).year
        if (age in 1..99)
            mAge = age
        myMarkerView = MyMarkerView(this, R.layout.module_heart_rate_chat)
        initLineChart()
        initBarChart()
        setData()
    }

    private fun initLineChart() {
        val maxHeart = 220 - mAge
        h0 = (maxHeart * 0.5).toInt()
        h1 = (maxHeart * 0.6).toInt()
        h2 = (maxHeart * 0.7).toInt()
        h3 = (maxHeart * 0.83).toInt()
        h4 = (maxHeart * 0.91).toInt()

        with(lineChart) {
            description.isEnabled = false
            legend.textSize = 16f
            setNoDataText("无运动数据")
            animateX(2500)

            myMarkerView.chartView = this
            marker = myMarkerView
        }

        with(lineChart.xAxis) {
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM
            setAvoidFirstLastClipping(true)
            setLabelCount(3, false)
            setValueFormatter { value, _ ->
                val d = TimeTransfer.utcMillion2Date(array[value.toInt()].utc)
                df.format(d)
            }
            textSize = 12f
        }

        with(lineChart.axisLeft) {
            setDrawGridLines(false)
            setLabelCount(5, false)
            textSize = 12f
            addLimitLine(LimitLine(h0.toFloat(), "放松").also {
                it.lineColor = resources.getColor(R.color.light_grey)
            })
            addLimitLine(LimitLine(h1.toFloat(), "热身").also {
                it.lineColor = resources.getColor(R.color.light_blue)
            })
            addLimitLine(LimitLine(h2.toFloat(), "基础").also {
                it.lineColor = resources.getColor(R.color.light_green)
            })
            addLimitLine(LimitLine(h3.toFloat(), "高效").also {
                it.lineColor = resources.getColor(R.color.light_orange)
            })
            addLimitLine(LimitLine(h4.toFloat(), "冲刺").also {
                it.lineColor = resources.getColor(R.color.light_red)
            })
            setDrawGridLinesBehindData(true)
        }

        with(lineChart.axisRight) {
            setDrawGridLines(false)
            setLabelCount(5, false)
            textSize = 12f
        }
    }

    private fun initBarChart() {
        val labelName = listOf("放松", "热身", "基础", "高效", "冲刺")
        barChart.apply {
            setNoDataText("无运动数据")
            description.isEnabled = false
            legend.isEnabled = false
            setExtraOffsets(20F, 20F, 20F, 20F)
            animateY(1400)
        }
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            labelCount = 5
            valueFormatter = IAxisValueFormatter { value, _ -> labelName[value.toInt()] }
            yOffset = 15F
        }
    }

    private fun setData() {
        SportsHeartTask(object : SportsHeartListener {
            override fun onSuccess(p: List<SportsHeart>) {
                for (i in p) {
                    array.add(i)
                }
                val entries = arrayListOf<Entry>()
                for (i in array.indices) {
                    entries.add(Entry((i).toFloat(), array[i].rate.toFloat()))
                }
                val set = LineDataSet(entries, "运动速心率曲线")
                set.apply {
                    axisDependency = YAxis.AxisDependency.LEFT
                    setDrawCircles(false)
                    setDrawValues(false)
                    lineWidth = 1f
                    color = resources.getColor(R.color.blue)
                    highLightColor = resources.getColor(R.color.orange)
                }
                lineChart.data = LineData(set)

                var t1 = 0
                var t2 = 0
                var t3 = 0
                var t4 = 0
                var t5 = 0
                for (i in array.indices) {
                    when {
                        array[i].rate <= h1 -> ++t1
                        array[i].rate <= h2 -> ++t2
                        array[i].rate <= h3 -> ++t3
                        array[i].rate <= h4 -> ++t4
                        else -> ++t5
                    }
                }
                array2.add(t1.toFloat() / 60)
                array2.add(t2.toFloat() / 60)
                array2.add(t3.toFloat() / 60)
                array2.add(t4.toFloat() / 60)
                array2.add(t5.toFloat() / 60)

                val entries2 = arrayListOf<BarEntry>()
                for (i in array2.indices) {
                    entries2.add(BarEntry(i.toFloat(), array2[i]))
                }
                val bSet = BarDataSet(entries2, "").apply {
                    valueTextSize = 12F
                    colors = getColors2()
                    valueFormatter = IValueFormatter { value, _, _, _ ->
                        "${String.format("%.2f", value)}min"
                    }
                }
                barChart.data = BarData(bSet)
            }

        }).execute(id)

        SportsPositionTask2(object : SportsPositionTask2Listener {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(p: SportsSummary) {
                maxHeart.text = "最高心率：${p.maxHeartRate}次/分"
                avgHeart.text = "平均心率：${p.avgHeartRate}次/分"
            }

        }).execute(id)
    }

    private fun getColors2() = arrayListOf(
        resources.getColor(R.color.light_grey),
        resources.getColor(R.color.light_blue),
        resources.getColor(R.color.light_green),
        resources.getColor(R.color.light_orange),
        resources.getColor(R.color.light_red)
    )

    private inner class MyMarkerView(context: Context, layoutResource: Int) :
        MarkerView(context, layoutResource) {
        private val mHeartRateTextView: TextView = findViewById(R.id.heart_rate_text_view)

        @SuppressLint("SetTextI18n")
        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            val date = TimeTransfer.utcMillion2Date(array[e!!.x.toInt()].utc)
            mHeartRateTextView.text = "${e.y} 次/分\t${df.format(date)}"
            super.refreshContent(e, highlight)
        }

        override fun getOffset(): MPPointF {
            return MPPointF(-15f, -height.toFloat())
        }
    }


    companion object {
        private const val SPORTS_ID = "sports_id"
        fun getIntent(context: Context, sportsId: Long) =
            Intent(context, SummaryOnceIndoorActivity::class.java).apply {
                putExtra(SPORTS_ID, sportsId)
            }
    }
}