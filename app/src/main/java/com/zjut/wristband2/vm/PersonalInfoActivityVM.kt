package com.zjut.wristband2.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.zjut.wristband2.R
import com.zjut.wristband2.util.SpUtil

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class PersonalInfoActivityVM(private val app: Application, private val handle: SavedStateHandle) :
    AndroidViewModel(app) {

    /**
     * student's id which require from sp in the first time and can survive when
     * the application is killed by OS
     */
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


    /**
     * student's name
     */
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

    /**
     * student's gender
     */
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

    /**
     * student's photo
     */
    val profile =
        if (sex == "男")
            app.getDrawable(R.drawable.ic_profile_boy)
        else
            app.getDrawable(R.drawable.ic_profile_girl)

    /**
     * student's birthday that defined as Live data
     * when the birthday change that the view can change in the meanwhile
     */
    val birthday by lazy {
        MutableLiveData<Long>().apply {
            value =
                SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getLong(SpUtil.SpAccount.BIRTHDAY, 0)
        }
    }

    /**
     * student's height
     */
    val height by lazy {
        MutableLiveData<Int>().apply {
            value =
                SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getInt(SpUtil.SpAccount.HEIGHT, 0)
        }
    }

    /**
     * student's weight
     */
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