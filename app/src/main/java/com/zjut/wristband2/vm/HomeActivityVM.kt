package com.zjut.wristband2.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.zjut.wristband2.R
import com.zjut.wristband2.util.SpUtil
import com.zjut.wristband2.util.TimeTransfer
import java.util.*

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
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


    /**
     * the status of device
     */
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

    /**
     * control a failed connection
     */
    var isConnecting: Boolean
        get() {
            if (!handle.contains(CONNECTING)) {
                handle.set(
                    CONNECTING,
                    false
                )
            }
            return handle[CONNECTING]!!
        }
        set(value) = handle.set(CONNECTING, value)


    var isBind: Boolean
        get() {
            if (!handle.contains(BIND)) {
                handle.set(
                    BIND,
                    false
                )
            }
            return handle[BIND]!!
        }
        set(value) = handle.set(BIND, value)


    /**
     * mac address
     */
    var address: String
        get() {
            if (!handle.contains(ADDRESS)) {
                handle.set(
                    ADDRESS,
                    SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getString(
                        SpUtil.SpAccount.MAC_ADDRESS,
                        ""
                    )
                )
            }
            return handle[ADDRESS]!!
        }
        set(value) = handle.set(ADDRESS, value)

    /**
     * device name
     */
    var typeName: String
        get() {
            if (!handle.contains(TYPE)) {
                handle.set(
                    TYPE,
                    SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getString(
                        SpUtil.SpAccount.MAC_NAME,
                        ""
                    )
                )
            }
            return handle[TYPE]!!
        }
        set(value) = handle.set(TYPE, value)

    /**
     * device type
     */
    var type: String
        get() {
            if (!handle.contains(TYPE_ID)) {
                handle.set(
                    TYPE_ID,
                    SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).getString(
                        SpUtil.SpAccount.MAC_TYPE,
                        ""
                    )
                )
            }
            return handle[TYPE_ID]!!
        }
        set(value) = handle.set(TYPE_ID, value)


    var step: Int
        get() {
            if (!handle.contains(STEP)) {
                var s =
                    SpUtil.getSp(SpUtil.SpStatistics.FILE_NAME).getInt(SpUtil.SpStatistics.STEP, 0)
                if (s != 0) {
                    val t =
                        SpUtil.getSp(SpUtil.SpStatistics.FILE_NAME)
                            .getLong(SpUtil.SpStatistics.UTC, 0)
                    val d = TimeTransfer.utc2Date(t)
                    val d1 = Date()
                    if (d.year != d1.year || d.month != d1.month || d.date != d1.date) {
                        s = 0
                    }
                }
                handle.set(STEP, s)
            }
            return handle[STEP]!!
        }
        set(value) = handle.set(STEP, value)


    companion object {
        private const val SID = "sid"
        private const val NAME = "name"
        private const val SEX = "sex"
        private const val CONNECT = "connect"
        private const val CONNECTING = "connecting"
        private const val BIND = "bind"
        private const val ADDRESS = "address"
        private const val TYPE = "type"
        private const val TYPE_ID = "typeId"
        private const val STEP = "step"
    }

}