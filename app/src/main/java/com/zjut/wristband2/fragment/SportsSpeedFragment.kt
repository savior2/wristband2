package com.zjut.wristband2.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

import com.zjut.wristband2.R
import com.zjut.wristband2.repo.SportsPosition
import com.zjut.wristband2.repo.SportsSummary
import com.zjut.wristband2.task.SportsPositionTask
import com.zjut.wristband2.task.SportsPositionTask2
import com.zjut.wristband2.task.SportsPositionTask2Listener
import com.zjut.wristband2.task.SportsPositionTaskListener
import com.zjut.wristband2.util.TimeTransfer
import com.zjut.wristband2.vm.SummaryOnceActivityVM
import kotlinx.android.synthetic.main.activity_summary_once.*
import kotlinx.android.synthetic.main.fragment_sports_speed.*
import java.text.SimpleDateFormat

/**
 * A simple [Fragment] subclass.
 */
class SportsSpeedFragment : Fragment() {

    private lateinit var viewModel: SummaryOnceActivityVM

    private lateinit var myMarkerView: MyMarkerView
    private val array = arrayListOf<Speed>()

    @SuppressLint("SimpleDateFormat")
    private val df = SimpleDateFormat("HH:mm:ss")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sports_speed, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SummaryOnceActivityVM::class.java]
        myMarkerView = MyMarkerView(requireContext(), R.layout.module_heart_rate_chat)
        initLineChart()
        setData()
    }

    private fun initLineChart() {
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
        }

        with(lineChart.axisRight) {
            setDrawGridLines(false)
            setLabelCount(5, false)
            textSize = 12f
        }
    }

    private fun setData() {
        SportsPositionTask(object : SportsPositionTaskListener {
            override fun onSuccess(p: List<SportsPosition>) {
                for (i in p) {
                    array.add(Speed(String.format("%.2f", i.speed).toFloat(), i.utc))
                }
                val entries = arrayListOf<Entry>()
                for (i in array.indices) {
                    entries.add(Entry((i).toFloat(), array[i].speed))
                }
                val set = LineDataSet(entries, "运动速度曲线")
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
        SportsPositionTask2(object : SportsPositionTask2Listener {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(p: SportsSummary) {
                runTimeText.text = "运动时间：${String.format(
                    "%02d:%02d:%02d",
                    p.exerciseTime / 3600,
                    p.exerciseTime % 3600 / 60,
                    p.exerciseTime % 60
                )}"
                runDistanceText.text = "运动距离：${String.format("%.2f", p.distance)}m"
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
            mHeartRateTextView.text = "${e.y}km/h\t${df.format(date)}"
            super.refreshContent(e, highlight)
        }

        override fun getOffset(): MPPointF {
            return MPPointF(-15f, -height.toFloat())
        }
    }

    private data class Speed(var speed: Float, var utc: Long)
}
