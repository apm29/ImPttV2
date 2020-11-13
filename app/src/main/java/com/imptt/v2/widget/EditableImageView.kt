package com.imptt.v2.widget

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.imptt.v2.R
import com.imptt.v2.di.GlideApp
import com.permissionx.guolindev.PermissionX
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy


class EditableImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(
    context, attrs, defStyleAttr
) {

    companion object {
        const val REQUEST_CODE_CHOOSE = 1878
    }

    private var mEditable:Boolean

    init {

        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.EditableImageView, defStyleAttr, 0
        )

        mEditable = typedArray.getBoolean(R.styleable.EditableImageView_editable, true)

        typedArray.recycle()
        updateUi(context)
    }

    private fun updateUi(context: Context) {
        if (drawable == null && mEditable) {
            setImageResource(R.drawable.layer_user_add)
        }
        if (mEditable) {
            setOnClickListener {
                doRequestPermissions {
                    showChooseDialog(context)
                }
            }
        }
    }

    fun setEditable(editable:Boolean){
        mEditable = editable
        updateUi(context)
    }

    private fun doRequestPermissions(
        permissions: List<String> = arrayListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
        ),
        callback: (() -> Unit)? = null
    ) {
        PermissionX.init(context as FragmentActivity).permissions(
            permissions
        )
            .explainReasonBeforeRequest()
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "App运行需要获取手机内部存储权限/相机权限",
                    "好的",
                    "取消"
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "请到设置中心打开存储权限/相机权限",
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

    var imageUrl:String? = null
        set(value) {
            field = value
            GlideApp.with(this)
                .load(value)
                .transform(RoundedCorners(15))
                .centerCrop()
                .into(this)
        }

    private fun showChooseDialog(context: Context) {
        Dialog(::onResult).show(
            (context as FragmentActivity).supportFragmentManager,
            "choose image"
        )
    }

    private fun onResult(uri: MutableList<Uri>, path: MutableList<String>) {
        if (uri.isNotEmpty()) {
            imageUrl = path.first()
            GlideApp.with(this)
                .load(uri.first())
                .transform(RoundedCorners(15))
                .centerCrop()
                .into(this)
        }
    }


    class Dialog(val onResult: (MutableList<Uri>, MutableList<String>) -> Unit) : DialogFragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragemnt_choose_image, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            takePhoto(view)
        }

        private fun chooseFromGallery(view: View) {
            Matisse.from(this)
                .choose(MimeType.ofAll())
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.75f)
                .showSingleMediaType(true)
                .imageEngine(Glide4Engine())
                .forResult(REQUEST_CODE_CHOOSE)
        }

        private fun takePhoto(view: View) {
            Matisse.from(this)
                .choose(MimeType.ofImage())
                .capture(true)
                .captureStrategy(
                    CaptureStrategy(true, "com.imptt.v2.fileprovider", "test")
                )
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.75f)
                .imageEngine(Glide4Engine())
                .showSingleMediaType(true)
                .forResult(REQUEST_CODE_CHOOSE)
        }


        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            println("requestCode = [${requestCode}], resultCode = [${resultCode}], data = [${data}]")
            if (requestCode == REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
                val uri = Matisse.obtainResult(data)
                val path = Matisse.obtainPathResult(data)
                onResult(uri, path)
            }
            dismiss()
        }
    }

}
