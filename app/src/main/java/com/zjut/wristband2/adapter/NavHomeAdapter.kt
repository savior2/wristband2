package com.zjut.wristband2.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zjut.wristband2.R
import com.zjut.wristband2.activity.DailyHeartActivity
import com.zjut.wristband2.activity.SportsSummaryActivity
import kotlinx.android.synthetic.main.activity_version.view.*
import kotlinx.android.synthetic.main.cell_nav_home.view.*


class NavHomeAdapter(private val context: Context, private val array: List<Item>) :
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

                    }
                    4 -> {
                        val view2 =
                            LayoutInflater.from(context).inflate(R.layout.activity_version, null)
                        view2.version.text = "该功能正在积极开发..."
                        AlertDialog.Builder(context)
                            .setView(view2)
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
}


class NavHomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
