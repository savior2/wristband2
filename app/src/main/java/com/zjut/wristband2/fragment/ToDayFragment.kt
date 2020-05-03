package com.zjut.wristband2.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter

import com.zjut.wristband2.R
import com.zjut.wristband2.repo.SportsHeart
import com.zjut.wristband2.repo.SportsSummary
import com.zjut.wristband2.task.SportsHeartListener
import com.zjut.wristband2.task.SportsHeartTask
import com.zjut.wristband2.task.SummaryOneDayTask
import com.zjut.wristband2.task.SummaryOneDayTaskListener
import com.zjut.wristband2.util.TimeTransfer
import kotlinx.android.synthetic.main.fragment_to_day.*

/**
 * A simple [Fragment] subclass.
 */
class ToDayFragment : Fragment() {

    private var mWeight = 65
    private var mAge = 24
    private var h0 = 0
    private var h1 = 0
    private var h2 = 0
    private var h3 = 0
    private var h4 = 0
    private val array = arrayListOf<Float>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_to_day, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initBarChart()
        initPieChart()
        setData()
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
        val maxHeart = 220 - mAge

        h0 = (maxHeart * 0.5).toInt()
        h1 = (maxHeart * 0.6).toInt()
        h2 = (maxHeart * 0.7).toInt()
        h3 = (maxHeart * 0.83).toInt()
        h4 = (maxHeart * 0.91).toInt()

    }

    private fun initPieChart() {
        pieChart.apply {
            setNoDataText("无运动数据")
            centerText = "运动数据分布"
            description.isEnabled = false
            legend.isEnabled = false
            animateY(1400, Easing.EasingOption.EaseInOutQuad)
            rotationAngle = -15f
            setExtraOffsets(26F, 5F, 26F, 5F)
            setUsePercentValues(true)
            setEntryLabelColor(Color.BLACK)
        }
    }


    private fun setData() {
        val span = TimeTransfer.getTodayTimeSpan()
        SummaryOneDayTask(object : SummaryOneDayTaskListener {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(p: List<SportsSummary>) {
                var sum = 0;
                var exerciseTime = 0F
                var exerciseDistance = 0F
                val num = arrayListOf<Long>()
                for (i in p) {
                    sum += 1;
                    exerciseTime += i.exerciseTime
                    exerciseDistance += i.distance
                    num.add(i.id.toLong())
                }
                textView9.text = "运动次数：$sum"
                textView10.text = "运动时长：${String.format("%.2f", exerciseTime / 60)}min"
                textView11.text =
                    "消耗热量：${String.format("%.2f", mWeight * exerciseDistance * 1.036 / 1000)}kcal"
                textView12.text = "运动总路程：${String.format("%.2f", exerciseDistance / 1000)}km"
                SportsHeartTask(object : SportsHeartListener {
                    override fun onSuccess(p: List<SportsHeart>) {
                        var t1 = 0
                        var t2 = 0
                        var t3 = 0
                        var t4 = 0
                        var t5 = 0
                        for (i in p.indices) {
                            when {
                                p[i].rate <= h1 -> ++t1
                                p[i].rate <= h2 -> ++t2
                                p[i].rate <= h3 -> ++t3
                                p[i].rate <= h4 -> ++t4
                                else -> ++t5
                            }
                        }
                        array.add(t1.toFloat() / 60)
                        array.add(t2.toFloat() / 60)
                        array.add(t3.toFloat() / 60)
                        array.add(t4.toFloat() / 60)
                        array.add(t5.toFloat() / 60)
                        setBarData()
                        setPieData()
                    }

                }).execute(*num.toTypedArray())
            }
        }).execute(span.startTime, span.endTime)
    }

    private fun setBarData() {
        val entries = arrayListOf<BarEntry>()
        for (i in array.indices) {
            entries.add(BarEntry(i.toFloat(), array[i]))
        }
        val bSet = BarDataSet(entries, "").apply {
            valueTextSize = 12F
            colors = getColors2()
            valueFormatter = IValueFormatter { value, _, _, _ ->
                "${String.format("%.2f", value)}min"
            }
        }
        barChart.data = BarData(bSet)
    }

    private fun setPieData() {
        val entries = arrayListOf<PieEntry>()
        val color = arrayListOf<Int>()
        val labelName = listOf("放松", "热身", "基础", "高效", "冲刺")
        for (i in array.indices) {
            if (array[i] > 0.01) {
                entries.add(PieEntry(array[i], labelName[i]))
                color.add(getColors2()[i])
            }
        }

        val set = PieDataSet(entries, "").apply {
            colors = color
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            valueLineColor = Color.LTGRAY
            valueTextColor = Color.DKGRAY
            valueTextSize = 10F
            sliceSpace = 1f
            isHighlightEnabled = true
            valueFormatter = IValueFormatter { value, _, _, _ ->
                "${String.format("%.2f", value)}%"
            }
        }
        pieChart.data = PieData(set)
    }

    private fun getColors2() = arrayListOf(
        resources.getColor(R.color.light_grey),
        resources.getColor(R.color.light_blue),
        resources.getColor(R.color.light_green),
        resources.getColor(R.color.light_orange),
        resources.getColor(R.color.light_red)
    )
}
