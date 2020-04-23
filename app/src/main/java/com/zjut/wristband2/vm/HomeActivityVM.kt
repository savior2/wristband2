package com.zjut.wristband2.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.zjut.wristband2.R
import com.zjut.wristband2.util.SpUtil


class HomeActivityVM(private val app: Application, private val handle: SavedStateHandle) :
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


    var isConnected: Boolean
        get() {
            if (!handle.contains(CONNECT)) {
                handle.set(
                    CONNECT,
                    false
                )
            }
            return handle[CONNECT]!!
        }
        set(value) = handle.set(CONNECT, value)


    var address: String
        get() {
            if (!handle.contains(ADDRESS)) {
                handle.set(
                    ADDRESS,
                    ""
                )
            }
            return handle[ADDRESS]!!
        }
        set(value) = handle.set(ADDRESS, value)

    var typeName: String
        get() {
            if (!handle.contains(TYPE)) {
                handle.set(
                    TYPE,
                    ""
                )
            }
            return handle[TYPE]!!
        }
        set(value) = handle.set(TYPE, value)

    companion object {
        private const val SID = "sid"
        private const val NAME = "name"
        private const val SEX = "sex"
        private const val CONNECT = "connect"
        private const val ADDRESS = "address"
        private const val TYPE = "type"
    }

}