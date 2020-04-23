package com.zjut.wristband2.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.zjut.wristband2.R
import com.zjut.wristband2.adapter.Item
import com.zjut.wristband2.adapter.NavBorderAdapter
import com.zjut.wristband2.adapter.NavHomeAdapter
import kotlinx.android.synthetic.main.fragment_nav_home.*

/**
 * A simple [Fragment] subclass.
 */
class NavHomeFragment : Fragment() {

    private val array = arrayListOf(
        Item(1, "日常心率", R.drawable.ic_nav_home_heart),
        Item(2, "累计运动", R.drawable.ic_nav_home_all),
        Item(3, "日常运动", R.drawable.ic_nav_home_one),
        Item(4, "有氧能力", R.drawable.ic_nav_home_health)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nav_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        colorProgressBar.setCurrentValues(7000f)
        recyclerView.apply {
            val manager = LinearLayoutManager(requireContext())
            manager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = manager
            addItemDecoration(DividerItemDecoration(this@NavHomeFragment.requireContext(),DividerItemDecoration.VERTICAL))
            adapter = NavHomeAdapter(this@NavHomeFragment.requireContext(), array)
        }
    }

}
