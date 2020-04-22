package com.zjut.wristband2.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zjut.wristband2.R
import kotlinx.android.synthetic.main.fragment_nav_home.*

/**
 * A simple [Fragment] subclass.
 */
class NavHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nav_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        colorProgressBar.setCurrentValues(7000f)
    }

}
