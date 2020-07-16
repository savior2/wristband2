package com.zjut.wristband2.activity

import android.Manifest
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import cn.sharesdk.onekeyshare.OnekeyShare
import com.zjut.wristband2.MyApplication.Companion.context
import com.zjut.wristband2.R
import com.zjut.wristband2.repo.Version
import com.zjut.wristband2.service.DownloadService
import com.zjut.wristband2.task.SimpleTaskListener
import com.zjut.wristband2.task.VersionTask
import com.zjut.wristband2.util.toast
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    var binder: DownloadService.DownloadBinder? = null
    private lateinit var url: String

    private val connect = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as DownloadService.DownloadBinder
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        with(toolbar) {
            navigationIcon = getDrawable(R.drawable.ic_menu)
            setNavigationOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        val nav = Navigation.findNavController(this, R.id.fragment)
        val config = AppBarConfiguration.Builder(bottomNavigationView.menu).build()
        NavigationUI.setupActionBarWithNavController(this, nav, config)
        NavigationUI.setupWithNavController(bottomNavigationView, nav)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_feedback -> {
                startActivity(Intent(this, FeedbackActivity::class.java))
            }
            R.id.item_version -> {
                VersionTask(object : SimpleTaskListener<Version?> {
                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun onSuccess(p: Version?) {
                        if (p != null) {
                            url = p.url
                            val intent =
                                Intent(this@HomeActivity, DownloadService::class.java)
                            startService(intent)
                            bindService(intent, connect, Context.BIND_AUTO_CREATE)
                            AlertDialog.Builder(this@HomeActivity)
                                .setTitle("当前有新版本可用：${p.newVersion}")
                                .setMessage("更新信息：${p.updateInfo}")
                                .setPositiveButton("下载") { _, _ ->
                                    if (ContextCompat.checkSelfPermission(
                                            this@HomeActivity,
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
                                .setNegativeButton("取消") { _, _ -> }
                                .create().show()
                        } else {
                            val intent = Intent(this@HomeActivity, VersionActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }).execute()
            }
            R.id.item_share -> {
                val oks = OnekeyShare().apply {
                    text = "测试"
                    setUrl("http://www.meetpanda.xyz")
                }
                oks.show(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun download() {
        toast(this, "开始下载")
        binder?.start(url)
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
}
