package com.zjut.wristband2.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.zjut.wristband2.R
import com.zjut.wristband2.repo.SportsHeart
import com.zjut.wristband2.task.SportsHeartListener
import com.zjut.wristband2.task.SportsHeartTask
import com.zjut.wristband2.util.TimeTransfer
import com.zjut.wristband2.vm.SummaryOnceActivityVM
import kotlinx.android.synthetic.main.activity_summary_once.*
import kotlinx.android.synthetic.main.fragment_sports_heart.*
import java.text.SimpleDateFormat

/**
 * A simple [Fragment] subclass.
 */
class SportsHeartFragment : Fragment() {

    private lateinit var viewModel: SummaryOnceActivityVM

    private lateinit var myMarkerView: MyMarkerView
    private val array = arrayListOf<SportsHeart>()

    private var mAge = 24
    private var h0 = 0
    private var h1 = 0
    private var h2 = 0
    private var h3 = 0
    private var h4 = 0

    @SuppressLint("SimpleDateFormat")
    private val df = SimpleDateFormat("HH:mm:ss")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sports_heart, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SummaryOnceActivityVM::class.java]
        myMarkerView = MyMarkerView(requireContext(), R.layout.module_heart_rate_chat)
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
            }

        }).execute(viewModel.id)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().viewPager2.isUserInputEnabled = false
    }

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
}
