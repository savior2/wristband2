package com.zjut.wristband2.util

import android.content.Context
import android.content.SharedPreferences
import com.zjut.wristband2.MyApplication

/**
 * @author qpf
 * @date 2020-8
 * @description manage the sp
 */
object SpUtil {

    fun getSp(file: String): SharedPreferences =
        MyApplication.context.getSharedPreferences(file, Context.MODE_PRIVATE)

    class SpAccount {
        companion object {
            const val FILE_NAME = "account"
            const val SID = "student_id"                //String
            const val PASSWORD = "password"             //String
            const val NAME = "name"                     //String
            const val SEX = "sex"                       //String
            const val BIRTHDAY = "birthday"             //Long
            const val HEIGHT = "height"                 //Int
            const val WEIGHT = "weight"                 //Float
            const val TOKEN = "token"                   //String
            const val MAC_ADDRESS = "mac_address"       //String
            const val MAC_TYPE = "mac_type"             //String
            const val MAC_NAME = "mac_name"             //String
        }
    }

    class SpStatistics {
        companion object {
            const val FILE_NAME = "statistics"
            const val UTC = "utc"                       //Long(10位时间戳)
            const val STEP = "step"                     //Int
            const val IP_ADDRESS = "ip_address"         //String
        }
    }
}