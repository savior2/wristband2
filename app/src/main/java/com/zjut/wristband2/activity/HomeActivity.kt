package com.zjut.wristband2.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import cn.sharesdk.onekeyshare.OnekeyShare
import com.zjut.wristband2.R
import com.zjut.wristband2.repo.Version
import com.zjut.wristband2.task.SimpleTaskListener
import com.zjut.wristband2.task.VersionTask
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

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
                    override fun onSuccess(p: Version?) {
                        val intent = Intent(this@HomeActivity, VersionActivity::class.java)
                        Log.e("test", "result:$p")
                        if (p != null) {
                            intent.putExtra("latest", false)
                            intent.putExtra("data", p)
                        } else {
                            intent.putExtra("latest", true)
                        }
                        startActivity(intent)
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
}
