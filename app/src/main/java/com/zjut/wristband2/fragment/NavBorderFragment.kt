package com.zjut.wristband2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zjut.wristband2.R
import com.zjut.wristband2.adapter.Item
import com.zjut.wristband2.adapter.NavBorderAdapter
import com.zjut.wristband2.databinding.FragmentNavBorderBinding
import com.zjut.wristband2.vm.HomeActivityVM

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class NavBorderFragment : Fragment() {

    private lateinit var viewModel: HomeActivityVM
    private lateinit var binding: FragmentNavBorderBinding
    private val array = arrayListOf(
        Item(1, "个人信息", R.drawable.ic_nav_border_person),
        Item(2, "修改密码", R.drawable.ic_nav_border_security),
        Item(3, "手环管理", R.drawable.ic_nav_border_device),
        Item(4, "关于东南健行", R.drawable.ic_nav_border_info),
        Item(5, "退出登录", R.drawable.ic_nav_border_exit)
//        Item(6, "系统测试", R.drawable.ic_nav_border_test)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_nav_border, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(),
            SavedStateViewModelFactory(requireActivity().application, requireActivity())
        )[HomeActivityVM::class.java]
        binding.data = viewModel
        binding.lifecycleOwner = requireActivity()
        binding.recyclerView.apply {
            val manager = LinearLayoutManager(requireContext())
            manager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = manager
            adapter = NavBorderAdapter(this@NavBorderFragment.requireContext(), array)
        }
    }

}
