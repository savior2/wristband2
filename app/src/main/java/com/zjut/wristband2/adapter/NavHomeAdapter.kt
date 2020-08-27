package com.zjut.wristband2.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.*
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.zjut.wristband2.R
import com.zjut.wristband2.activity.DailyHeartActivity
import com.zjut.wristband2.activity.SportsSummaryActivity
import com.zjut.wristband2.activity.SummaryOneDayActivity
import com.zjut.wristband2.repo.MyDatabase
import com.zjut.wristband2.task.PickDateTask
import com.zjut.wristband2.task.PickDateTaskListener
import kotlinx.android.synthetic.main.cell_nav_home.view.*
import kotlinx.android.synthetic.main.module_wheel_date_picker.view.*
import java.util.*

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class NavHomeAdapter(
    private val context: Context,
    private val array: List<Item>,
    private val parent: View,
    private val window: Window
) :
    RecyclerView.Adapter<NavHomeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavHomeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cell_nav_home, parent, false)
        return NavHomeViewHolder(view).apply {
            view.setOnClickListener {
                when (array[this.adapterPosition].id) {
                    1 -> {
                        context.startActivity(Intent(context, DailyHeartActivity::class.java))
                    }
                    2 -> {
                        context.startActivity(Intent(context, SportsSummaryActivity::class.java))
                    }
                    3 -> {
                        normalStatic()
                    }
                    4 -> {
                        AlertDialog.Builder(context)
                            .setTitle("你的有氧运动能力为：良好")
                            .setPositiveButton("确定") { _, _ -> }
                            .create().show()
                    }
                }
            }
        }
    }

    override fun getItemCount() = array.size

    override fun onBindViewHolder(holder: NavHomeViewHolder, position: Int) {
        with(holder.itemView) {
            imageView.setImageDrawable(context.getDrawable(array[position].image))
            textView.text = array[position].text
        }
    }

    private fun normalStatic() {
        val view = LayoutInflater.from(context).inflate(R.layout.module_wheel_date_picker, null)
        with(Calendar.getInstance()) {
            time = Date()
            view.date_picker.init(
                get(Calendar.YEAR),
                get(Calendar.MONTH),
                get(Calendar.DAY_OF_MONTH),
                null
            )
        }
        val mPopupWindow = PopupWindow(
            view,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        ).apply {
            isOutsideTouchable = true
            isFocusable = true
            setBackgroundDrawable(BitmapDrawable())
            animationStyle = R.style.AnimTheme
            showAtLocation(
                parent,
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
                0,
                0
            )
            setShade(0.7F)
            setOnDismissListener {
                setShade(1F)
            }
        }
        view.cancel.setOnClickListener {
            mPopupWindow.dismiss()
        }
        view.confirm.setOnClickListener {
            mPopupWindow.dismiss()
            val startUtc = GregorianCalendar(
                view.date_picker.year,
                view.date_picker.month,
                view.date_picker.dayOfMonth
            ).time.time
            PickDateTask(object : PickDateTaskListener {
                override fun onSuccess(p: Boolean) {
                    if (p) {
                        AlertDialog.Builder(context)
                            .setTitle("该日无运动数据！")
                            .setPositiveButton("确定") { _, _ -> }
                            .create().show()
                    } else {
                        context.startActivity(SummaryOneDayActivity.getIntent(context, startUtc))
                    }
                }
            }).execute(startUtc, startUtc + 86400000)
        }
    }

    private fun setShade(value: Float) {
        val lp: WindowManager.LayoutParams = window.attributes
        lp.alpha = value
        window.attributes = lp
    }
}


class NavHomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
