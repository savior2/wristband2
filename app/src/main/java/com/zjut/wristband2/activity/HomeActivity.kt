package com.zjut.wristband2.activity

import android.Manifest
import android.app.AlertDialog
import android.content.*
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
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import cn.sharesdk.onekeyshare.OnekeyShare
import com.zjut.wristband2.R
import com.zjut.wristband2.receiver.NetworkReceiver
import com.zjut.wristband2.repo.Version
import com.zjut.wristband2.service.DownloadService
import com.zjut.wristband2.task.SimpleTaskListener
import com.zjut.wristband2.task.VersionTask
import com.zjut.wristband2.util.isNetworkConnected
import com.zjut.wristband2.util.toast
import com.zjut.wristband2.vm.HomeActivityVM
import kotlinx.android.synthetic.main.activity_home.*

/**
 * @author qpf
 * @date 2020-8
 * @description
 */
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

    private lateinit var viewModel: HomeActivityVM

    private lateinit var networkListener: NetworkReceiver

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        viewModel = ViewModelProvider(
            this,
            SavedStateViewModelFactory(application, this)
        )[HomeActivityVM::class.java]
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

        networkListener = NetworkReceiver(object : NetworkReceiver.NetworkListener {
            override fun doWork() {
                runOnUiThread(
                    Thread{
                        AlertDialog.Builder(this@HomeActivity)
                            .setCancelable(false)
                            .setTitle("登录失效，请重新登录！")
                            .setPositiveButton("确定") { _, _ ->
                                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                                this@HomeActivity.finish()
                            }
                            .create().show()
                    }
                )
            }
        })
        with(IntentFilter()) {
            addAction("android.net.conn.CONNECTIVITY_CHANGE")
            registerReceiver(networkListener, this)
        }
        /*with(SpUtil.getSp(SpUtil.SpAccount.FILE_NAME)) {
            DeviceUtil.startDataReceive(
                getString(SpUtil.SpAccount.MAC_TYPE, "")!!,
                getString(SpUtil.SpAccount.MAC_ADDRESS, "")!!,
                MyDataCallback(viewModel, null)
            )
        }*/
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
                        if (!isNetworkConnected()) {
                            toast(this@HomeActivity, "网络连接异常！")
                        } else if (p != null) {
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkListener)
    }
}
