package com.zjut.wristband2.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zjut.wristband2.R
import com.zjut.wristband2.activity.SummaryOnceActivity
import com.zjut.wristband2.activity.SummaryOnceIndoorActivity
import com.zjut.wristband2.repo.SportsSummary
import com.zjut.wristband2.util.TimeTransfer
import kotlinx.android.synthetic.main.cell_sports_one_day.view.*
import java.text.SimpleDateFormat


class SportsOneDayAdapter(
    private val array: List<SportsSummary>,
    private val context: Context
) :
    RecyclerView.Adapter<SportsOneDayViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportsOneDayViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cell_sports_one_day, parent, false)
        return SportsOneDayViewHolder(view).apply {
            view.setOnClickListener {
                if (array[this.adapterPosition].mode == "indoor") {
                    context.startActivity(
                        SummaryOnceIndoorActivity.getIntent(
                            context,
                            array[this.adapterPosition].id.toLong()
                        )
                    )
                } else {
                    context.startActivity(
                        SummaryOnceActivity.getIntent(
                            context,
                            array[this.adapterPosition].id.toLong()
                        )
                    )
                }
            }
        }

    }

    override fun getItemCount() = array.size

    override fun onBindViewHolder(holder: SportsOneDayViewHolder, position: Int) {
        holder.itemView.timeText.text =
            df.format(TimeTransfer.utcMillion2Date(array[position].startUtc))
        if (array[position].mode == "indoor") {
            holder.itemView.imageView4.setImageDrawable(context.getDrawable(R.drawable.ic_weightlift))
            holder.itemView.type.text = "室内"
        }
    }

    @SuppressLint("SimpleDateFormat")
    private val df = SimpleDateFormat(" HH:mm:ss")
}


class SportsOneDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}