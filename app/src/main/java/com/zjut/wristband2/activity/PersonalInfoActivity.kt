package com.zjut.wristband2.activity

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.aigestudio.wheelpicker.WheelPicker
import com.zjut.wristband2.R
import com.zjut.wristband2.databinding.ActivityPersonalInfoBinding
import com.zjut.wristband2.util.SpUtil
import com.zjut.wristband2.util.TimeTransfer
import com.zjut.wristband2.vm.HomeActivityVM
import com.zjut.wristband2.vm.PersonalInfoActivityVM
import kotlinx.android.synthetic.main.activity_personal_info.*
import kotlinx.android.synthetic.main.module_wheel_date_picker.view.*
import kotlinx.android.synthetic.main.module_wheel_date_picker.view.cancel
import kotlinx.android.synthetic.main.module_wheel_date_picker.view.confirm
import kotlinx.android.synthetic.main.module_wheel_date_picker.view.title_text
import kotlinx.android.synthetic.main.module_wheel_height.view.*
import kotlinx.android.synthetic.main.module_wheel_weight.view.*
import java.text.SimpleDateFormat
import java.util.*

class PersonalInfoActivity : AppCompatActivity() {

    private lateinit var viewModel: PersonalInfoActivityVM
    private lateinit var binding: ActivityPersonalInfoBinding

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_personal_info)
        viewModel = ViewModelProvider(
            this,
            SavedStateViewModelFactory(application, this)
        )[PersonalInfoActivityVM::class.java]
        binding.data = viewModel
        binding.lifecycleOwner = this
        with(binding.toolbar) {
            navigationIcon = getDrawable(R.drawable.ic_menu_back)
            setNavigationOnClickListener {
                finish()
            }
        }

        viewModel.birthday.observe(this, Observer {
            val f = SimpleDateFormat("yyyy-MM-dd")
            binding.birthdayText.text = f.format(TimeTransfer.utcMillion2Date(it))

        })
        binding.birthdayLayout.setOnClickListener {
            pickDate()
        }
        binding.heightLayout.setOnClickListener {
            pickHeight()
        }
        binding.weightLayout.setOnClickListener {
            pickWeight()
        }
    }

    private fun pickDate() {
        val view = LayoutInflater.from(this).inflate(R.layout.module_wheel_date_picker, null)
        view.title_text.text = "出生日期"
        with(Calendar.getInstance()) {
            time = TimeTransfer.utcMillion2Date(viewModel.birthday.value!!)
            view.date_picker.init(
                get(Calendar.YEAR),
                get(Calendar.MONTH),
                get(Calendar.DAY_OF_MONTH),
                null
            )
        }
        val mPopupWindow = PopupWindow(
            view,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        ).apply {
            isOutsideTouchable = true
            isFocusable = true
            setBackgroundDrawable(BitmapDrawable())
            animationStyle = R.style.AnimTheme
            showAtLocation(
                binding.root,
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
                0,
                0
            )
            setShade(0.7F)
            setOnDismissListener {
                setShade(1F)
            }
        }
        view.cancel.setOnClickListener {
            mPopupWindow.dismiss()
        }
        view.confirm.setOnClickListener {
            mPopupWindow.dismiss()
            val time = GregorianCalendar(
                view.date_picker.year,
                view.date_picker.month,
                view.date_picker.dayOfMonth
            ).time.time
            viewModel.birthday.value = time
            SpUtil.getSp(SpUtil.SpAccount.FILE_NAME)
                .edit()
                .putLong(SpUtil.SpAccount.BIRTHDAY, time)
                .apply()
        }
    }


    private fun pickHeight() {
        val view =
            LayoutInflater.from(this).inflate(R.layout.module_wheel_height, null)
        val heightPicker = view.height_wheel_view.apply {
            val data2 = arrayListOf<Int>()
            for (i in 100..250) {
                data2.add(i)
            }
            data = data2
            if (viewModel.height.value != 0) {
                setSelectedItemPosition(viewModel.height.value!! - 100, false)
            }
        }

        val mPopupWindow = PopupWindow(
            view,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        ).apply {
            isOutsideTouchable = true
            isFocusable = true
            setBackgroundDrawable(BitmapDrawable())
            animationStyle = R.style.AnimTheme
            showAtLocation(
                binding.root,
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
                0,
                0
            )
            setShade(0.7F)
            setOnDismissListener {
                setShade(1F)
            }
        }
        view.cancel.setOnClickListener {
            mPopupWindow.dismiss()
        }

        view.confirm.setOnClickListener {
            mPopupWindow.dismiss()
            viewModel.height.value = heightPicker.currentItemPosition + 100
            SpUtil.getSp(SpUtil.SpAccount.FILE_NAME)
                .edit()
                .putInt(SpUtil.SpAccount.HEIGHT, heightPicker.currentItemPosition + 100)
                .apply()
        }
    }

    private fun pickWeight() {
        val view =
            LayoutInflater.from(this).inflate(R.layout.module_wheel_weight, null)
        val weightPicker1 = view.weight_wheel_picker1.apply {
            val data1 = arrayListOf<Int>()
            for (i in 40..120) {
                data1.add(i)
            }
            data = data1
            if (viewModel.weight.value!! > 1F) {
                setSelectedItemPosition(viewModel.weight.value!!.toInt() - 40, false)
            }
        }

        val weightPicker2 = view.weight_wheel_picker2.apply {
            val data2 = arrayListOf<Int>()
            for (i in 0..9) {
                data2.add(i)
            }
            data = data2
            if (viewModel.weight.value!! > 1F) {
                setSelectedItemPosition((viewModel.weight.value!! * 10).toInt() % 10, false)
            }
        }
        val mPopupWindow = PopupWindow(
            view,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        ).apply {
            isOutsideTouchable = true
            isFocusable = true
            setBackgroundDrawable(BitmapDrawable())
            animationStyle = R.style.AnimTheme
            showAtLocation(
                binding.root,
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
                0,
                0
            )
            setShade(0.7F)
            setOnDismissListener {
                setShade(1F)
            }
        }
        view.cancel.setOnClickListener {
            mPopupWindow.dismiss()
        }

        view.confirm.setOnClickListener {
            mPopupWindow.dismiss()
            val w1 = weightPicker1.currentItemPosition + 40
            val w2 = weightPicker2.currentItemPosition
            val w = w1 + (w2 / 10F)
            viewModel.weight.value = w
            SpUtil.getSp(SpUtil.SpAccount.FILE_NAME)
                .edit()
                .putFloat(SpUtil.SpAccount.WEIGHT, w)
                .apply()
        }

    }

    private fun setShade(value: Float) {
        val lp: WindowManager.LayoutParams = window.attributes
        lp.alpha = value
        window.attributes = lp
    }
}