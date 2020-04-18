package com.zjut.wristband2.util

import android.content.Context
import com.zjut.wristband2.MyApplication

object SpUtil {

    fun getSp(file: String) = MyApplication.context.getSharedPreferences(file, Context.MODE_PRIVATE)

    class SpAccount {
        companion object {
            val FILE_NAME = "account"
            val SID = "student_id"                //String
            val PASSWORD = "password"             //String
            val NAME = "name"                     //String
            val SEX = "sex"                       //String
            val BIRTHDAY = "birthday"             //Long
            val HEIGHT = "height"                 //Int
            val WEIGHT = "weight"                 //Float
            val TOKEN = "token"                   //String
            val MAC_ADDRESS = "mac_address"       //String
        }
    }

    class SpStatistics {
        companion object {
            val FILE_NAME = "statistics"
            val UTC = "utc"                       //Long
            val STEP = "step"                     //Int
            val IP_ADDRESS = "ip_address"         //String
        }
    }
}