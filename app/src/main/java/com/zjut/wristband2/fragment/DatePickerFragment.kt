package com.zjut.wristband2.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider

import com.zjut.wristband2.R
import com.zjut.wristband2.vm.DailyHeartActivityVM
import kotlinx.android.synthetic.main.fragment_date_picker.view.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class DatePickerFragment : DialogFragment() {

    private lateinit var myViewModel: DailyHeartActivityVM
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        myViewModel = ViewModelProvider(requireActivity())[DailyHeartActivityVM::class.java]
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_date_picker, null)
        with(Calendar.getInstance()) {
            time = myViewModel.date.value!!
            (view.date_picker as DatePicker).init(
                this.get(Calendar.YEAR),
                this.get(Calendar.MONTH),
                this.get(Calendar.DAY_OF_MONTH),
                null
            )
        }
        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setNegativeButton("取消") { _, _ -> }
            .setPositiveButton("确定") { _, _ ->
                val d = GregorianCalendar(
                    view.date_picker.year,
                    view.date_picker.month,
                    view.date_picker.dayOfMonth
                ).time
                myViewModel.setDate(d)
            }
            .create()
    }
}
