package com.zjut.wristband2.util

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.zjut.wristband2.MyApplication
import com.zjut.wristband2.error.WCode
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

object WebUtil {
    private const val KEY = "oldShouHuan511ok"
    fun login(sid: String, password: String): WCode {
        if (!isNetworkConnected()) {
            return WCode.NetworkError
        }
        val keyContent = AesUtil.AESEncode(KEY, "$sid&$password")
        WebBasic.doPost(
            WebBasic.DOMAIN + WebBasic.LOGIN_URI,
            mapOf("content" to keyContent)
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
        val keyContent = AesUtil.AESEncode(KEY, "$sid&$newPassword&$newPassword&$verifyCode")
        WebBasic.doPost(
            WebBasic.DOMAIN + WebBasic.RESET_PASSWORD_URI,
            mapOf(
                "content" to keyContent
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

    fun modifyPassword(
        sid: String,
        oldPassword: String,
        newPassword: String
    ): WCode {
        if (!isNetworkConnected()) {
            return WCode.NetworkError
        }
        val keyContent = AesUtil.AESEncode(KEY, "$sid&$oldPassword&$newPassword&$newPassword")
        WebBasic.doPost(
            WebBasic.DOMAIN + WebBasic.MODIFY_PASSWORD_URI,
            mapOf(
                "content" to keyContent
            )
        ) {
            try {
                val jsonObject = JSONObject(it)
                when (jsonObject.getString("Code")) {
                    "0" -> return WCode.OK
                    "4" -> return WCode.PasswordError
                }
            } catch (e: JSONException) {
                return WCode.JsonParseError
            } catch (e: Exception) {
                return WCode.ServerError
            }
        }
        return WCode.OK
    }

    fun feedback(
        sid: String,
        name: String,
        type: String,
        content: String,
        contact: String
    ): WCode {
        if (!isNetworkConnected()) {
            return WCode.NetworkError
        }
        WebBasic.doPost(
            WebBasic.DOMAIN + WebBasic.FEEDBACK_URI,
            mapOf(
                "userid" to sid,
                "username" to name,
                "type" to type,
                "content" to content,
                "contact" to contact
            )
        ) {
            try {
                val jsonObject = JSONObject(it)
                when (jsonObject.getString("code")) {
                    "0" -> return WCode.OK
                }
            } catch (e: JSONException) {
                return WCode.JsonParseError
            } catch (e: Exception) {
                return WCode.ServerError
            }
        }
        return WCode.OK
    }

    fun postAerobics(body: String) =
        basicPost(WebBasic.DOMAIN_POST_TEMP + WebBasic.POST_AEROBICS_URI, body)

    fun postNormalSports(body: String) =
        basicPost(WebBasic.DOMAIN_POST_TEMP + WebBasic.POST_NORMAL_SPORTS_URI, body)

    private fun basicPost(url: String, body: String): WCode {
        if (!isNetworkConnected()) {
            return WCode.NetworkError
        }
        WebBasic.doPostWithRawBody(url, body) {
            try {
                val jsonObject = JSONObject(it)
                when (jsonObject.getString("Code")) {
                    "0" -> {
                        return WCode.OK
                    }
                    "6" -> {
                        return WCode.TokenInvalidError
                    }
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
    private val JSON_TYPE = "application/json; charset=utf-8".toMediaType()
    const val DOMAIN = "http://www.justrun.com.cn"
    const val DOMAIN_POST_TEMP = "http://47.99.157.159:9898"
    const val LOGIN_URI = "/api/sportsEquipment/getConnectServlet"
    const val VERIFY_CODE_URI = "/api/sportsEquipment/getVertifyCode"
    const val RESET_PASSWORD_URI = "/api/sportsEquipment/resetPassword"
    const val MODIFY_PASSWORD_URI = "/api/sportsEquipment/modifyPSWServlet"
    const val FEEDBACK_URI = "/api/sportsEquipment/feedback"
    const val POST_AEROBICS_URI = "/uploadTestData"
    const val POST_NORMAL_SPORTS_URI = "/uploadSportData"

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

    inline fun doPostWithRawBody(url: String, body: String, callable: (String) -> Unit) {
        val requestBody = RequestBody.create(JSON_TYPE, body)
        val request = Request.Builder().url(url).post(requestBody).build()
        val response = OkHttpClient().newCall(request).execute()
        callable(response.body?.string() ?: "")
    }
}