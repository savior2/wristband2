package com.zjut.wristband2

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        _context = applicationContext
    }

    companion object {
        private lateinit var _context: Context
        val context get() = _context
    }
}