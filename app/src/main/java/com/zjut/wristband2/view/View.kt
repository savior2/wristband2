package com.zjut.wristband2.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 * @author qpf
 * @date 2020-8
 * @description test, not use
 */
class MyViewPager(private val context2: Context, private val attributeSet: AttributeSet) :
    ViewPager(context2, attributeSet) {

    override fun canScroll(v: View?, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        if (((v!!)::class as Any).javaClass.name =="com.baidu.mapapi.map.MapView")
            return true
        return super.canScroll(v, checkV, dx, x, y)
    }
}
