package com.zjut.wristband2.activity

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.zjut.wristband2.R
import com.zjut.wristband2.fragment.ToDayFragment
import com.zjut.wristband2.fragment.ToMonthFragment
import com.zjut.wristband2.fragment.ToWeekFragment
import kotlinx.android.synthetic.main.activity_sports_summary.*

class SportsSummaryActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sports_summary)
        with(toolbar) {
            navigationIcon = getDrawable(com.zjut.wristband2.R.drawable.ic_menu_back)
            setNavigationOnClickListener {
                finish()
            }
            viewPager2.adapter = object : FragmentStateAdapter(this@SportsSummaryActivity) {
                override fun getItemCount() = 3

                override fun createFragment(position: Int) = when (position) {
                    0 -> ToDayFragment()
                    1 -> ToWeekFragment()
                    else -> ToMonthFragment()
                }
            }
        }
        TabLayoutMediator(tabLayout, viewPager2) { tab, pos ->
            when (pos) {
                0 -> tab.text = "本日"
                1 -> tab.text = "本周"
                else -> tab.text = "本月"
            }
        }.attach()
    }
}
