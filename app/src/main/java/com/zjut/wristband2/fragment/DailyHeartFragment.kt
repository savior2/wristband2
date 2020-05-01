package com.zjut.wristband2.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
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
import com.zjut.wristband2.repo.DailyHeart
import com.zjut.wristband2.task.DailyHeartTask
import com.zjut.wristband2.task.SimpleTaskListener
import com.zjut.wristband2.util.TimeTransfer
import com.zjut.wristband2.vm.DailyHeartActivityVM
import kotlinx.android.synthetic.main.fragment_daily_heart.*
import java.text.SimpleDateFormat

/**
 * A simple [Fragment] subclass.
 */

class DailyHeartFragment : Fragment() {

    private lateinit var myViewModel: DailyHeartActivityVM
    private lateinit var myMarkerView: MyMarkerView
    private lateinit var array: List<DailyHeart>

    @SuppressLint("SimpleDateFormat")
    private val df1 = SimpleDateFormat("yyyy年MM月dd日")
    @SuppressLint("SimpleDateFormat")
    private val df2 = SimpleDateFormat("HH:mm")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_heart, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        myViewModel = ViewModelProvider(requireActivity())[DailyHeartActivityVM::class.java]
        myMarkerView = MyMarkerView(requireContext(), R.layout.module_heart_rate_chat)
        pickDate.setOnClickListener {
            val nav = Navigation.findNavController(it)
            nav.navigate(R.id.action_dailyHeartFragment_to_datePickerFragment)
        }
        initLineChart()
        myViewModel.date.observe(requireActivity(), Observer {
            reset()
        })
    }

    private fun initLineChart() {
        with(lineChart) {
            description.isEnabled = false
            legend.textSize = 16f
            setNoDataText("今日未作记录")
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
                val d = TimeTransfer.utc2Date(array[value.toInt()].utc)
                df2.format(d)
            }
            textSize = 12f
        }

        with(lineChart.axisLeft) {
            setDrawGridLines(false)
            setLabelCount(5, false)
            textSize = 12f
            addLimitLine(LimitLine(72f, "").also { it.lineColor = Color.GREEN })
            addLimitLine(LimitLine(100f, "").also { it.lineColor = Color.RED })
            addLimitLine(LimitLine(60f, "").also { it.lineColor = Color.GRAY })
            setDrawGridLinesBehindData(true)
        }

        with(lineChart.axisRight) {
            setDrawGridLines(false)
            setLabelCount(5, false)
            textSize = 12f
        }
    }

    private fun reset() {
        lineChart.apply {
            clear()
            animateX(2500)
        }
        DailyHeartTask(object : SimpleTaskListener {
            override fun onSuccess(list: List<DailyHeart>) {
                if (list.isEmpty()) return
                array = list
                val entries = arrayListOf<Entry>()
                for (i in list.indices) {
                    entries.add(Entry((i).toFloat(), list[i].heartRate.toFloat()))
                }
                val set = LineDataSet(entries, "${df1.format(myViewModel.date.value!!)} 心率数据")
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
        }).execute(myViewModel.date.value)
    }


    private inner class MyMarkerView(context: Context, layoutResource: Int) :
        MarkerView(context, layoutResource) {
        private val mHeartRateTextView: TextView = findViewById(R.id.heart_rate_text_view)
        @SuppressLint("SetTextI18n")
        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            val date = TimeTransfer.utc2Date(array[e!!.x.toInt()].utc)
            mHeartRateTextView.text = "${e.y}次/分\t${df2.format(date)}"
            super.refreshContent(e, highlight)
        }

        override fun getOffset(): MPPointF {
            return MPPointF(-15f, -height.toFloat())
        }
    }
}
