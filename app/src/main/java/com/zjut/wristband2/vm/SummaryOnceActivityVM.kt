package com.zjut.wristband2.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.zjut.wristband2.repo.MyDatabase

class SummaryOnceActivityVM(app: Application) : AndroidViewModel(app) {

    var id = 0L

    val hearts by lazy {
        MyDatabase.instance.getSportsHeartDao().findBySportsId2(id)
    }

    val positions by lazy {
        MyDatabase.instance.getSportsPositionDao().findBySportsId(id)
    }
}