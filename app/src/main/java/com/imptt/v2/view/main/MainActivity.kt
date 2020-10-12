package com.imptt.v2.view.main

import android.Manifest
import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.imptt.v2.R
import com.imptt.v2.core.messenger.connections.MESSAGE_TYPE_ECHO_TEST
import com.imptt.v2.core.messenger.connections.MessageFactory
import com.imptt.v2.core.messenger.view.ViewMessenger
import com.imptt.v2.core.struct.ServiceBindActivity
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ServiceBindActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        findViewById<FloatingActionButton>(R.id.fabHome).setOnClickListener {
            ViewMessenger.send(Message().apply {
                this.what = MESSAGE_TYPE_ECHO_TEST
                this.obj = Bundle().apply {
                    putString("TEST","Toast from View")
                }
                this.replyTo = ViewMessenger.myself()
            })
        }

        doRequestPermissions {
            Log.e("MainActivity","权限获取成功")
        }
    }

    private fun doRequestPermissions(
        permissions: List<String> = arrayListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ),
        callback:(()->Unit)? = null
    ) {
        PermissionX.init(this).permissions(
            permissions
        )
            .explainReasonBeforeRequest()
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "App运行需要获取手机内部存储权限以及录音权限！",
                    "好的",
                    "取消"
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "请到设置中心打开所需的权限",
                    "好的",
                    "取消"
                )
            }
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    callback?.invoke()
                } else {
                    doRequestPermissions(deniedList)
                }
            }
    }
}