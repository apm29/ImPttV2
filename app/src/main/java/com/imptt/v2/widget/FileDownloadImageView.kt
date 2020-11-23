package com.imptt.v2.widget

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.imptt.v2.R
import com.imptt.v2.data.ImDataBase
import com.imptt.v2.di.GlideApp
import com.imptt.v2.utils.FileUtils
import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.StatusUtil
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.permissionx.guolindev.PermissionX
import java.io.File
import java.util.*


class FileDownloadImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(
    context, attrs, defStyleAttr
), DownloadListener {

    companion object {
        //建立一个文件类型与文件后缀名的匹配表
        private val MATCH_ARRAY = arrayOf(
            arrayOf(".3gp", "video/3gpp"),
            arrayOf(".apk", "application/vnd.android.package-archive"),
            arrayOf(".asf", "video/x-ms-asf"),
            arrayOf(".avi", "video/x-msvideo"),
            arrayOf(".bin", "application/octet-stream"),
            arrayOf(".bmp", "image/bmp"),
            arrayOf(".c", "text/plain"),
            arrayOf(".class", "application/octet-stream"),
            arrayOf(".conf", "text/plain"),
            arrayOf(".cpp", "text/plain"),
            arrayOf(".doc", "application/msword"),
            arrayOf(".docx", "application/msword"),
            arrayOf(".xls", "application/msword"),
            arrayOf(".xlsx", "application/msword"),
            arrayOf(".exe", "application/octet-stream"),
            arrayOf(".gif", "image/gif"),
            arrayOf(".gtar", "application/x-gtar"),
            arrayOf(".gz", "application/x-gzip"),
            arrayOf(".h", "text/plain"),
            arrayOf(".htm", "text/html"),
            arrayOf(".html", "text/html"),
            arrayOf(".jar", "application/java-archive"),
            arrayOf(".java", "text/plain"),
            arrayOf(".jpeg", "image/jpeg"),
            arrayOf(".jpg", "image/jpeg"),
            arrayOf(".js", "application/x-javascript"),
            arrayOf(".log", "text/plain"),
            arrayOf(".m3u", "audio/x-mpegurl"),
            arrayOf(".m4a", "audio/mp4a-latm"),
            arrayOf(".m4b", "audio/mp4a-latm"),
            arrayOf(".m4p", "audio/mp4a-latm"),
            arrayOf(".m4u", "video/vnd.mpegurl"),
            arrayOf(".m4v", "video/x-m4v"),
            arrayOf(".mov", "video/quicktime"),
            arrayOf(".mp2", "audio/x-mpeg"),
            arrayOf(".mp3", "audio/x-mpeg"),
            arrayOf(".mp4", "video/mp4"),
            arrayOf(".mpc", "application/vnd.mpohun.certificate"),
            arrayOf(".mpe", "video/mpeg"),
            arrayOf(".mpeg", "video/mpeg"),
            arrayOf(".mpg", "video/mpeg"),
            arrayOf(".mpg4", "video/mp4"),
            arrayOf(".mpga", "audio/mpeg"),
            arrayOf(".msg", "application/vnd.ms-outlook"),
            arrayOf(".ogg", "audio/ogg"),
            arrayOf(".pdf", "application/pdf"),
            arrayOf(".png", "image/png"),
            arrayOf(".pps", "application/vnd.ms-powerpoint"),
            arrayOf(".ppt", "application/vnd.ms-powerpoint"),
            arrayOf(".prop", "text/plain"),
            arrayOf(".rar", "application/x-rar-compressed"),
            arrayOf(".rc", "text/plain"),
            arrayOf(".rmvb", "audio/x-pn-realaudio"),
            arrayOf(".rtf", "application/rtf"),
            arrayOf(".sh", "text/plain"),
            arrayOf(".tar", "application/x-tar"),
            arrayOf(".tgz", "application/x-compressed"),
            arrayOf(".txt", "text/plain"),
            arrayOf(".wav", "audio/x-wav"),
            arrayOf(".wma", "audio/x-ms-wma"),
            arrayOf(".wmv", "audio/x-ms-wmv"),
            arrayOf(".wps", "application/vnd.ms-works"),
            arrayOf(".xml", "text/plain"),
            arrayOf(".z", "application/x-compress"),
            arrayOf(".zip", "application/zip"),
            arrayOf("", "*/*")
        )
    }

    private val fileMessageDao = ImDataBase.getInstance(context).getFileMessageDao()

    init {

        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.FileDownloadImageView, defStyleAttr, 0
        )
        typedArray.recycle()
        updateUi(context)
    }

    var missionId: String? = null
        set(value) {
            field = value
            updateUi(context)
        }

    var task: DownloadTask? = null


    private fun updateUi(context: Context) {
        if (missionId != null) {
            val fileMessage = fileMessageDao
                .getFileMessageById(missionId!!)
            if (fileMessage.localPath != null) {
                if (fileMessage.type == 1) {
                    this.imageUrl = fileMessage.localPath
                } else {
                    maxWidth = 100
                    maxHeight = 100
                    GlideApp.with(this)
                        .load(R.mipmap.ic_file)
                        .override(100, 100)
                        .fitCenter()
                        .into(this)
                }
                setOnClickListener {
                    openFileByPath(context, fileMessage.localPath, Intent.ACTION_VIEW)
                }
                setOnLongClickListener {
                    openFileByPath(context, fileMessage.localPath,Intent.ACTION_SEND)
                    true
                }
                return
            } else {
                if (fileMessage.type == 1) {
                    this.imageUrl = fileMessage.content
                } else {
                    maxWidth = 100
                    maxHeight = 100
                    GlideApp.with(this)
                        .load(R.mipmap.download)
                        .override(100, 100)
                        .into(this)
                }
                setOnClickListener {
                    doRequestPermissions {
                        if (task != null) {
                            val status = StatusUtil.getStatus(task!!)
                            when (status) {
                                StatusUtil.Status.COMPLETED -> {
                                    if (fileMessage.type == 1) {
                                        this.imageUrl = fileMessage.localPath
                                    }
                                    return@doRequestPermissions
                                }
                                StatusUtil.Status.PENDING -> {
                                    loadingIcon()
                                    return@doRequestPermissions
                                }
                                StatusUtil.Status.RUNNING -> {
                                    loadingIcon()
                                    return@doRequestPermissions
                                }
                                StatusUtil.Status.IDLE -> {
                                    loadingIcon()
                                }
                                StatusUtil.Status.UNKNOWN -> {
                                    loadingIcon()
                                }
                            }
                        }
                        task = DownloadTask.Builder(
                            fileMessage.content, FileUtils.getDownloadsDir()
                        ).setFilename(
                            "${System.currentTimeMillis()}_DOWNLOAD" + fileMessage.fileName
                        )
                            .setMinIntervalMillisCallbackProcess(100)
                            .setPassIfAlreadyCompleted(true)
                            .build()
                        task?.enqueue(this)
                        loadingIcon()
                    }
                }
            }

        }

    }

    private fun loadingIcon() {
        GlideApp.with(this)
            .load(R.mipmap.giphy)
            .into(this)
    }

    private fun doRequestPermissions(
        permissions: List<String> = arrayListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
                    "App运行需要获取手机内部存储权限",
                    "好的",
                    "取消"
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "请到设置中心打开存储权限",
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

    private var imageUrl: String? = null
        set(value) {
            field = value
            println("value = $value $this")
            maxWidth = if (width > 100) {
                width
            } else {
                context.resources.displayMetrics.widthPixels * 0.6.toInt()
            }
            maxHeight = context.resources.displayMetrics.heightPixels
            GlideApp.with(this)
                .load(value)
                .placeholder(R.mipmap.ic_image)
                .override(width, height)
                .into(this)
        }

    override fun taskStart(task: DownloadTask) {
        Toast.makeText(this.context, "开始下载", Toast.LENGTH_SHORT).show()
    }

    override fun connectTrialStart(
        task: DownloadTask,
        requestHeaderFields: MutableMap<String, MutableList<String>>
    ) {

    }

    override fun connectTrialEnd(
        task: DownloadTask,
        responseCode: Int,
        responseHeaderFields: MutableMap<String, MutableList<String>>
    ) {
    }

    override fun downloadFromBeginning(
        task: DownloadTask,
        info: BreakpointInfo,
        cause: ResumeFailedCause
    ) {
    }

    override fun downloadFromBreakpoint(task: DownloadTask, info: BreakpointInfo) {
    }

    override fun connectStart(
        task: DownloadTask,
        blockIndex: Int,
        requestHeaderFields: MutableMap<String, MutableList<String>>
    ) {
    }

    override fun connectEnd(
        task: DownloadTask,
        blockIndex: Int,
        responseCode: Int,
        responseHeaderFields: MutableMap<String, MutableList<String>>
    ) {
    }

    override fun fetchStart(task: DownloadTask, blockIndex: Int, contentLength: Long) {
    }

    override fun fetchProgress(task: DownloadTask, blockIndex: Int, increaseBytes: Long) {

    }

    override fun fetchEnd(task: DownloadTask, blockIndex: Int, contentLength: Long) {
    }

    override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: Exception?) {
        println(cause)
        realCause?.printStackTrace()
        Toast.makeText(this.context, "下载完成：${task.filename}", Toast.LENGTH_SHORT).show()
        val fileMessage = fileMessageDao
            .getFileMessageById(missionId!!)
        fileMessage.localPath = task.file?.absolutePath
        fileMessage.localUri = task.uri.toString()
        fileMessageDao.updateMessageById(fileMessage)
        println(fileMessage)
        updateUi(context)
    }

    /**
     * 根据路径打开文件
     * @param context 上下文
     * @param path 文件路径
     */
    @SuppressLint("ShowToast")
    fun openFileByPath(context: Context?, path: String?, action: String?) {
        if (context == null || path == null) return
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //设置intent的Action属性
        intent.action = action ?:Intent.ACTION_VIEW
        //文件的类型
        var type = ""
        for (i in MATCH_ARRAY.indices) {
            //判断文件的格式
            if (path.toString().toLowerCase(Locale.getDefault()).contains(MATCH_ARRAY[i][0])) {
                type = MATCH_ARRAY[i][1]
                break
            }
        }
        try {
            val uri =
                FileProvider.getUriForFile(context, "com.imptt.v2.fileprovider", File(path))
            //设置intent的data和Type属性
            intent.setDataAndType(uri, type)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //跳转
            context.startActivity(intent)
        } catch (e: java.lang.Exception) { //当系统没有携带文件打开软件，提示
            Toast.makeText(context, "无法打开该格式文件!", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }


}
