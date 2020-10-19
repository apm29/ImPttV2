package com.imptt.v2.view

import android.Manifest
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.imptt.v2.R
import com.imptt.v2.core.messenger.connections.*
import com.imptt.v2.core.messenger.view.ViewMessenger
import com.imptt.v2.core.struct.ServiceBindActivity
import com.permissionx.guolindev.PermissionX

/**
 *  author : ciih
 *  date : 2020/10/19 10:50 AM
 *  description :
 */
class HostActivity : ServiceBindActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)


        doRequestPermissions {
            Log.e("MainActivity", "权限获取成功")
        }

        ViewMessenger.on(MESSAGE_TYPE_IN_CALL) {
            //获取group列表
            val groupId =
                it.data.getString(MESSAGE_DATA_KEY_GROUP_ID)
            val from =
                it.data.getString(MESSAGE_DATA_KEY_FROM_USER_ID)
            Toast.makeText(this, "收到 $from 在 $groupId 的呼叫", Toast.LENGTH_SHORT).show()
        }.on(MESSAGE_TYPE_MESSAGE) {
            Toast.makeText(
                this,
                it.data.getString(MESSAGE_DATA_KEY_MESSAGE),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.app_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }

    private fun doRequestPermissions(
        permissions: List<String> = arrayListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ),
        callback: (() -> Unit)? = null
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