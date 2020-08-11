package com.zjut.wristband2.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.zjut.wristband2.R
import com.zjut.wristband2.adapter.Item
import com.zjut.wristband2.adapter.NavBorderAdapter
import com.zjut.wristband2.adapter.NavHomeAdapter
import com.zjut.wristband2.vm.HomeActivityVM
import kotlinx.android.synthetic.main.fragment_nav_home.*

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class NavHomeFragment : Fragment() {

    private val array = arrayListOf(
        Item(1, "日常心率", R.drawable.ic_nav_home_heart),
        Item(2, "累计运动", R.drawable.ic_nav_home_all),
        Item(3, "运动数据", R.drawable.ic_nav_home_one),
        Item(4, "有氧能力", R.drawable.ic_nav_home_health)
    )

    private lateinit var parent2: View

    private lateinit var viewModel: HomeActivityVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parent2 = inflater.inflate(R.layout.fragment_nav_home, container, false)
        return parent2
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(),
            SavedStateViewModelFactory(requireActivity().application, requireActivity())
        )[HomeActivityVM::class.java]
        colorProgressBar.setCurrentValues(viewModel.step.toFloat())
        colorProgressBar.setOnClickListener {
            colorProgressBar.setCurrentValues(viewModel.step.toFloat())
        }
        recyclerView.apply {
            val manager = LinearLayoutManager(requireContext())
            manager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = manager
            addItemDecoration(
                DividerItemDecoration(
                    this@NavHomeFragment.requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = NavHomeAdapter(
                this@NavHomeFragment.requireContext(),
                array,
                parent2,
                requireActivity().window
            )
        }
    }

}
