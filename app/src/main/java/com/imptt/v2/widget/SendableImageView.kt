package com.imptt.v2.widget

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
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
import com.imptt.v2.utils.FileUtils
import com.permissionx.guolindev.PermissionX
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import com.zhihu.matisse.internal.utils.MediaStoreCompat
import com.zhihu.matisse.ui.MatisseActivity
import java.util.*


class SendableImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(
    context, attrs, defStyleAttr
) {

    companion object {
        const val REQUEST_CODE_CHOOSE = 1878
        const val CALL_CAMERA_ACTIVITY_REQUEST_CODE = 1879
        const val CALL_FILER_ACTIVITY_REQUEST_CODE = 1880
    }

    private val type: Int

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.SendableImageView, defStyleAttr, 0
        )

        type = typedArray.getInt(R.styleable.SendableImageView_type, 1)
        println("type = $type")
        typedArray.recycle()
        updateUi(context)
    }

    private fun updateUi(context: Context) {
        if (drawable == null) {
            setImageResource(R.mipmap.ic_image)
        }
        setOnClickListener {
            doRequestPermissions {
                showChooseDialog(context)
            }
        }
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

    var imageUrl: String? = null
        set(value) {
            field = value
            GlideApp.with(this)
                .load(value)
                .transform(RoundedCorners(15))
                .centerCrop()
                .into(this)
        }

    private fun showChooseDialog(context: Context) {
        Dialog(::onResult, type).show(
            (context as FragmentActivity).supportFragmentManager,
            "choose image"
        )
    }

    private fun onResult(uri: MutableList<Uri>, path: MutableList<String>) {
        if (uri.isNotEmpty()) {
            callback?.invoke(uri, path)
        }
    }

    var callback: ((MutableList<Uri>, MutableList<String>) -> Unit)? = null



    class Dialog(
        val onResult: (MutableList<Uri>, MutableList<String>) -> Unit,
        val type: Int
    ) : DialogFragment() {

        private lateinit var mMediaStoreCompat: MediaStoreCompat

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragemnt_choose_image, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            when (type) {
                1 -> {
                    takePhoto(view)
                }
                2 -> {
                    chooseFromGallery(view)
                }
                3 -> {
                    getFile()
                }
            }
        }

        private fun chooseFromGallery(view: View) {
            Matisse.from(this)
                .choose(MimeType.ofAll())
                .capture(false)
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.75f)
                .showSingleMediaType(true)
                .imageEngine(Glide4Engine())
                .forResult(REQUEST_CODE_CHOOSE)
        }

        private fun takePhoto(view: View) {
//            Matisse.from(this)
//                .choose(MimeType.ofImage())
//                .capture(true)
//                .captureStrategy(
//                    CaptureStrategy(true, "com.imptt.v2.fileprovider", "test")
//                )
//                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//                .thumbnailScale(0.75f)
//                .imageEngine(Glide4Engine())
//                .showSingleMediaType(true)
//                .forResult(REQUEST_CODE_CHOOSE)


//            val dir = File(Environment.getExternalStorageDirectory(), "pictures")
//            if (dir.exists()) {
//                dir.mkdirs()
//            }
//            val currentImageFile = File(dir, System.currentTimeMillis().toString() + ".webp")
//            if (!currentImageFile.exists()) {
//                try {
//                    currentImageFile.createNewFile()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//            val it = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile))
//            startActivityForResult(it, Activity.DEFAULT_KEYS_DIALER)


            mMediaStoreCompat = MediaStoreCompat(this.requireActivity(), this)
            mMediaStoreCompat.setCaptureStrategy(
                CaptureStrategy(
                    true,
                    requireContext().packageName.toString() + ".fileprovider"
                )
            )
            mMediaStoreCompat.dispatchCaptureIntent(
                this.requireContext(),
                CALL_CAMERA_ACTIVITY_REQUEST_CODE
            )
        }

        private fun getFile() {
            val intent = Intent()
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.action = Intent.ACTION_OPEN_DOCUMENT
            intent.type = "*/*"
            val mimeTypes = arrayOf("*/*")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            startActivityForResult(
                Intent.createChooser(intent, "choose file"),
                CALL_FILER_ACTIVITY_REQUEST_CODE
            )
        }


        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            println("requestCode = [${requestCode}], resultCode = [${resultCode}], data = [${data}]")
            if (requestCode == REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
                val uri = Matisse.obtainResult(data)
                val path = Matisse.obtainPathResult(data)
                onResult(uri, path)
            } else if (requestCode == CALL_CAMERA_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

                // Just pass the data back to previous calling Activity.
                val contentUri: Uri = mMediaStoreCompat.currentPhotoUri
                val rawPath: String = mMediaStoreCompat.currentPhotoPath
                val selected = ArrayList<Uri>()
                selected.add(contentUri)
                val selectedPath = ArrayList<String>()
                selectedPath.add(rawPath)
                val result = Intent()
                result.putParcelableArrayListExtra(MatisseActivity.EXTRA_RESULT_SELECTION, selected)
                result.putStringArrayListExtra(
                    MatisseActivity.EXTRA_RESULT_SELECTION_PATH,
                    selectedPath
                )
                val uri = Matisse.obtainResult(result)
                val path = Matisse.obtainPathResult(result)
                onResult(uri, path)
            } else if (requestCode == CALL_FILER_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                val rawUri = data?.data
                if (rawUri != null) {
                    val contentUri: Uri = rawUri
                    val rawPath: String =  FileUtils.getPath(requireContext(),rawUri)
                    val selected = ArrayList<Uri>()
                    selected.add(contentUri)
                    val selectedPath = ArrayList<String>()
                    selectedPath.add(rawPath)
                    val result = Intent()
                    result.putParcelableArrayListExtra(
                        MatisseActivity.EXTRA_RESULT_SELECTION,
                        selected
                    )
                    result.putStringArrayListExtra(
                        MatisseActivity.EXTRA_RESULT_SELECTION_PATH,
                        selectedPath
                    )
                    val uri = Matisse.obtainResult(result)
                    val path = Matisse.obtainPathResult(result)
                    onResult(uri, path)
                }
            }
            dismiss()
        }

    }

}
