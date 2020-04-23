package com.zjut.wristband2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zjut.wristband2.R
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

                    }
                    2 -> {

                    }
                    3 -> {

                    }
                    4 -> {

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
