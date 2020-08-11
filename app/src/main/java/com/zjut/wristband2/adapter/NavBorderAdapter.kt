package com.zjut.wristband2.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zjut.wristband2.MyApplication
import com.zjut.wristband2.R
import com.zjut.wristband2.activity.*
import com.zjut.wristband2.util.SpUtil
import kotlinx.android.synthetic.main.cell_nav_border.view.*

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class NavBorderAdapter(private val context: Context, private val array: List<Item>) :
    RecyclerView.Adapter<MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cell_nav_border, parent, false)
        return MyHolder(view).apply {
            view.setOnClickListener {
                when (array[this.adapterPosition].id) {
                    1 -> {
                        context.startActivity(Intent(context, PersonalInfoActivity::class.java))
                    }
                    2 -> {
                        context.startActivity(Intent(context, ModifyPasswordActivity::class.java))
                    }
                    3 -> {
                        if (!MyApplication.isConnect) {
                            AlertDialog.Builder(context)
                                .setTitle("请先连接手环！")
                                .setPositiveButton("确定") { _, _ -> }
                                .create().show()
                        } else {
                            context.startActivity(Intent(context, DeviceManageActivity::class.java))
                        }
                    }
                    4 -> {
                        context.startActivity(Intent(context, AboutActivity::class.java))
                    }
                    5 -> {
                        AlertDialog.Builder(context)
                            .setTitle("确定退出？")
                            .setNegativeButton("取消") { _, _ -> }
                            .setPositiveButton("确定") { _, _ -> logout() }
                            .create().show()
                    }
                    6 -> {
                        context.startActivity(Intent(context, TestActivity::class.java))
                    }
                }
            }
        }
    }

    private fun logout() {
        with(SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).edit()) {
            putString(SpUtil.SpAccount.PASSWORD, "")
            apply()
        }
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    override fun getItemCount() = array.size

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        with(holder.itemView) {
            imageView.setImageDrawable(context.getDrawable(array[position].image))
            textView.text = array[position].text
            if (position == 4) {
                textView.setTextColor(context.resources.getColor(R.color.red))
            } else {
                textView.setTextColor(context.resources.getColor(R.color.dark_grey))
            }
        }
    }
}

class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
}


data class Item(val id: Int, val text: String, val image: Int)