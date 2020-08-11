package com.zjut.wristband2.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.zjut.wristband2.R
import com.zjut.wristband2.fragment.SportsHeartFragment
import com.zjut.wristband2.fragment.SportsSpeedFragment
import com.zjut.wristband2.fragment.SportsTraceFragment
import com.zjut.wristband2.vm.SummaryOnceActivityVM
import kotlinx.android.synthetic.main.activity_summary_once.*

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class SummaryOnceActivity : FragmentActivity() {

    private lateinit var viewmodel: SummaryOnceActivityVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this)[SummaryOnceActivityVM::class.java]
        viewmodel.id = intent.getLongExtra(SPORTS_ID, 0L)
        setContentView(R.layout.activity_summary_once)
        with(toolbar) {
            navigationIcon = getDrawable(com.zjut.wristband2.R.drawable.ic_menu_back)
            setNavigationOnClickListener {
                finish()
            }
            viewPager2.adapter = object : FragmentStateAdapter(this@SummaryOnceActivity) {
                override fun getItemCount() = 3

                override fun createFragment(position: Int) = when (position) {
                    0 -> SportsHeartFragment()
                    1 -> SportsSpeedFragment()
                    else -> SportsTraceFragment()
                }
            }
        }
        TabLayoutMediator(tabLayout, viewPager2) { tab, pos ->
            when (pos) {
                0 -> tab.text = "心率"
                1 -> tab.text = "速度"
                else -> tab.text = "轨迹"
            }
        }.attach()
    }

    companion object {
        private const val SPORTS_ID = "sports_id"
        fun getIntent(context: Context, sportsId: Long) =
            Intent(context, SummaryOnceActivity::class.java).apply {
                putExtra(SPORTS_ID, sportsId)
            }
    }
}
