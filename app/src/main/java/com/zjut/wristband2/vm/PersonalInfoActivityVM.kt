package com.zjut.wristband2.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.zjut.wristband2.R
import com.zjut.wristband2.util.SpUtil

class PersonalInfoActivityVM(private val app: Application, private val handle: SavedStateHandle) :
    AndroidViewModel(app) {

    val sid: String
        get() {
            if (!handle.contains(SID)) {
                handle.set(
                    SID,
                    SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getString(SpUtil.SpAccount.SID, "")
                )
            }
            return handle[SID]!!
        }


    val name: String
        get() {
            if (!handle.contains(NAME)) {
                handle.set(
                    NAME,
                    SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getString(SpUtil.SpAccount.NAME, "")
                )
            }
            return handle[NAME]!!
        }

    val sex: String
        get() {
            if (!handle.contains(SEX)) {
                handle.set(
                    SEX,
                    SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getString(SpUtil.SpAccount.SEX, "")
                )
            }
            return handle[SEX]!!
        }

    val profile =
        if (sex == "男")
            app.getDrawable(R.drawable.ic_profile_boy)
        else
            app.getDrawable(R.drawable.ic_profile_girl)

    val birthday by lazy {
        MutableLiveData<Long>().apply {
            value =
                SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getLong(SpUtil.SpAccount.BIRTHDAY, 0)
        }
    }

    val height by lazy {
        MutableLiveData<Int>().apply {
            value =
                SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getInt(SpUtil.SpAccount.HEIGHT, 0)
        }
    }

    val weight by lazy {
        MutableLiveData<Float>().apply {
            value =
                SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getFloat(SpUtil.SpAccount.WEIGHT, 0F)
        }
    }


    companion object {
        private const val SID = "sid"
        private const val NAME = "name"
        private const val SEX = "sex"
    }

}