package com.imptt.v2.vm

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.imptt.v2.data.entity.FileMessage
import com.imptt.v2.data.repo.ImRepository
import com.imptt.v2.utils.LocalStorage
import com.imptt.v2.view.adapter.MessageListAdapter
import com.kylindev.pttlib.db.ChatMessageBean
import com.kylindev.pttlib.service.InterpttService
import java.util.*
import kotlin.collections.ArrayList

/**
 *  author : ciih
 *  date : 2020/10/19 10:41 AM
 *  description :
 */
class GroupViewModel(
    private val imRepository: ImRepository,
    private val localStorage: LocalStorage
) : ViewModel() {

    private val pageSize = 20

    //已加载的语音消息
    private val voiceMessages: ArrayList<ChatMessageBean> = arrayListOf()

    //已加载的文本消息
    private val fileMessages: ArrayList<FileMessage> = arrayListOf()
    val mergeMessage: MutableLiveData<MutableList<Any>> = MutableLiveData(arrayListOf())

    fun loadMoreMessages(
        groupId: Int,
        service: InterpttService,
        refresh: Boolean = true,
        adapter: MessageListAdapter,
        layoutManager: RecyclerView.LayoutManager,
        handler: Handler
    ) {
        if (refresh) {
            voiceMessages.clear()
        }
        //本次加载的语音消息，
        val voiceMessage = service.loadDBRecords(groupId, voiceMessages.size, pageSize)
        //按时间戳范围获取文件
        val now = Date().time
        println("fileMessages.time = ${fileMessages.map { it.date.toLocaleString() }}")
        val timeEnd = if (refresh) now else {
            val voiceFirst = voiceMessages.firstOr(now) { it.time }
            println(
                "voiceMessage.last = ${Date(voiceFirst).toLocaleString()}"
            )
            val fileLast = fileMessages.lastOr(now) { it.date.time }
            println("fileMessage.last = ${Date(fileLast).toLocaleString()}")
            minOf(voiceFirst, fileLast)
            when(val lastMessage = adapter.messages.lastOrNull()){
                is ChatMessageBean-> lastMessage.time
                is FileMessage -> lastMessage.date.time
                else -> now
            }
        }
        //根据timeEnd获取历史File消息数据库中时间小于timeEnd的20条（limit）
        val last20FileMessageThanEnd = imRepository.getFileMessageWithTimeMax(timeEnd, groupId)
        println("timeEnd = ${Date(timeEnd).toLocaleString()}")
        println("last20FileMessageThanEnd = ${last20FileMessageThanEnd.joinToString{
            it.date.toLocaleString()
        }
        }")
        val fileMessageCount = imRepository.getCount(groupId)
        println("fileMessageCount = ${fileMessageCount}")
        val lastReadTime =
            if (last20FileMessageThanEnd.isNullOrEmpty() || fileMessageCount == 0) 0 else last20FileMessageThanEnd.last().date.time
        println("lastReadTime = ${Date(lastReadTime).toLocaleString()}")
        val timeStart = minOf(lastReadTime,voiceMessage.firstOr(0) { it.time })
        println("timeStart = ${Date(timeStart).toLocaleString()}")
        println("timeRange = $timeStart $timeEnd")
        val fileMessage = imRepository.getFileMessageWithTimeRange(timeStart, timeEnd, groupId)
        println("fileMessages = ${fileMessage.map { it.date.toLocaleString() }}")
        val mergeList = arrayListOf<Any>()
        mergeList.addAll(fileMessage)
        mergeList.addAll(voiceMessage ?: arrayListOf())
        //按时间排序
        mergeList.sortWith { one, another ->
            val timeOne = when (one) {
                is FileMessage -> {
                    one.date.time
                }
                is ChatMessageBean -> {
                    one.time
                }
                else -> {
                    0
                }
            }
            val timeAnother = when (another) {
                is FileMessage -> {
                    another.date.time
                }
                is ChatMessageBean -> {
                    another.time
                }
                else -> {
                    0
                }
            }
            (timeOne - timeAnother).toInt()
        }
        println(
            "mergeList.time = ${
                mergeList.map {
                    when (it) {
                        is FileMessage -> {
                            it.date.time
                        }
                        is ChatMessageBean -> {
                            it.time
                        }
                        else -> {
                            0
                        }
                    }
                }.map { 
                    Date(it).toLocaleString()
                }
            }"
        )
        if (voiceMessage != null && voiceMessage.isNotEmpty()) {
            voiceMessages.addAll(0, voiceMessage)
        }
        if (fileMessage.isNotEmpty()) {
            fileMessages.addAll(0, fileMessage)
        }
        //mergeMessage.value = mergeList.asReversed()
        val old = adapter.itemCount - 1
        if(refresh){
            adapter.resetData(mergeList.asReversed())
        }else {
            adapter.appendData(mergeList.asReversed())
        }
        if (!refresh)
            handler.postDelayed({
                println("old = $old")
                layoutManager.scrollToPosition(old)
            }, 50)
        else
            handler.postDelayed({
                layoutManager.scrollToPosition(0)
            }, 50)
        println("==============================================================================")
    }

    val loading: MutableLiveData<Boolean> = MutableLiveData(false)

    private fun <T, R> List<T>?.firstOr(onNull: R, get: (T) -> R): R {
        return if (isNullOrEmpty()) onNull else get(first())
    }

    private fun <T, R> List<T>?.lastOr(onNull: R, get: (T) -> R): R {
        return if (isNullOrEmpty()) onNull else get(last())
    }


}