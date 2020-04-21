package com.zjut.wristband2.util

import android.content.Context
import android.widget.Toast

fun toast(context: Context, s: String) =
    Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
