package com.zjut.wristband2.util

import android.content.Context
import android.widget.Toast

object WidgetUtil {
    fun toast(context: Context, s: String) =
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
}