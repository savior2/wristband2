package com.zjut.wristband2.activity

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.zjut.wristband2.R
import com.zjut.wristband2.repo.Version
import com.zjut.wristband2.service.DownloadService
import com.zjut.wristband2.util.toast
import kotlinx.android.synthetic.main.activity_version.*

class VersionActivity : AppCompatActivity() {

    var binder: DownloadService.DownloadBinder? = null

    private val connect = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as DownloadService.DownloadBinder
        }
    }

    private lateinit var url: String

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_version)
        val latest = intent.getBooleanExtra("latest", true)
        confirm.setOnClickListener {
            finish()
        }

        if (!latest) {
            val data = intent.getSerializableExtra("data") as Version
            url = data.url
            Log.e("test",url)
            version_info.text = "当前有新版本可用"
            version_num.text = data.newVersion
            confirm.text = "下载"
            val intent = Intent(this, DownloadService::class.java)
            startService(intent)
            bindService(intent, connect, Context.BIND_AUTO_CREATE)
            confirm.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        0
                    )
                } else {
                    download()
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    download()
                } else {
                    toast(this, "下载失败！请先授予相应权限！")
                }
            }
        }
    }

    private fun download() {
        toast(this, "开始下载")
        binder?.start(url)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
