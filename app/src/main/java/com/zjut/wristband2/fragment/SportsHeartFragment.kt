package com.zjut.wristband2.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zjut.wristband2.R
import kotlinx.android.synthetic.main.activity_summary_once.*

/**
 * A simple [Fragment] subclass.
 */
class SportsHeartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sports_heart, container, false)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().viewPager2.isUserInputEnabled = true
    }
}
