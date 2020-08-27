package com.zjut.wristband2.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
class SummaryOnceActivityVM(app: Application) : AndroidViewModel(app) {
    /**
     *the id of selected sports item
     */
    var id = 0L
}