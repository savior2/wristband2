package com.zjut.wristband2.util

import android.content.Context
import android.net.ConnectivityManager
import com.zjut.wristband2.MyApplication
import com.zjut.wristband2.error.WCode
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject

object WebUtil {
    fun login(sid: String, password: String): WCode {
        if (!isNetworkConnected()) {
            return WCode.NetworkError
        }
        WebBasic.doPost(
            WebBasic.DOMAIN + WebBasic.LOGIN_URI,
            mapOf("username" to sid, "password" to password)
        ) {
            try {
                val jsonObject = JSONObject(it)
                when (jsonObject.getString("Code")) {
                    "0" -> {
                        with(SpUtil.getSp(SpUtil.SpAccount.FILE_NAME).edit()) {
                            putString(SpUtil.SpAccount.SID, sid)
                            putString(SpUtil.SpAccount.PASSWORD, password)
                            putString(SpUtil.SpAccount.NAME, jsonObject.getString("StudentName"))
                            putString(SpUtil.SpAccount.SEX, jsonObject.getString("StudentSex"))
                            putString(SpUtil.SpAccount.TOKEN, jsonObject.getString("Token"))
                            apply()
                        }
                        return WCode.OK
                    }
                    "4" -> return WCode.AccountError
                }
            } catch (e: JSONException) {
                return WCode.JsonParseError
            } catch (e: Exception) {
                return WCode.ServerError
            }
        }
        return WCode.OK
    }

    fun getVerifyCode(sid: String): WCode {
        if (!isNetworkConnected()) {
            return WCode.NetworkError
        }
        WebBasic.doPost(
            WebBasic.DOMAIN + WebBasic.VERIFY_CODE_URI,
            mapOf("username" to sid)
        ) {
            try {
                val jsonObject = JSONObject(it)
                when (jsonObject.getString("Code")) {
                    "0" -> return WCode.OK
                    "1" -> return WCode.SendError
                    "1002" -> return WCode.EmailNotFoundError
                }
            } catch (e: JSONException) {
                return WCode.JsonParseError
            } catch (e: Exception) {
                return WCode.ServerError
            }
        }
        return WCode.OK
    }

    fun resetPassword(sid: String, newPassword: String, verifyCode: String): WCode {
        if (!isNetworkConnected()) {
            return WCode.NetworkError
        }
        WebBasic.doPost(
            WebBasic.DOMAIN + WebBasic.RESET_PASSWORD_URI,
            mapOf(
                "username" to sid,
                "newPassword" to newPassword,
                "newPassword2" to newPassword,
                "vertifyCode" to verifyCode
            )
        ) {
            try {
                val jsonObject = JSONObject(it)
                when (jsonObject.getString("Code")) {
                    "0" -> return WCode.OK
                    "1" -> return WCode.ResetError
                    "3" -> return WCode.DatabaseError
                    "1003" -> return WCode.VerifyCodeError
                }
            } catch (e: JSONException) {
                return WCode.JsonParseError
            } catch (e: Exception) {
                return WCode.ServerError
            }
        }
        return WCode.OK
    }
}


private fun isNetworkConnected(): Boolean {
    val connectivityManager =
        MyApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.activeNetworkInfo != null
}


private object WebBasic {
    const val DOMAIN = "http://www.justrun.com.cn"
    const val LOGIN_URI = "/api/sportsEquipment/getConnectServlet"
    const val VERIFY_CODE_URI = "/api/sportsEquipment/getVertifyCode"
    const val RESET_PASSWORD_URI = "/api/sportsEquipment/resetPassword"

    inline fun doPost(url: String, body: Map<String, String>, callable: (String) -> Unit) {
        val formBody = FormBody.Builder().apply {
            for ((k, v) in body) {
                add(k, v)
            }
        }.build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()
        val response = OkHttpClient()
            .newCall(request)
            .execute()
        callable(response.body?.string() ?: "")
    }
}