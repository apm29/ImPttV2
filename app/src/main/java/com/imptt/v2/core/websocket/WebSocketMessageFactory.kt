package com.imptt.v2.core.websocket

import com.google.gson.Gson
import com.imptt.v2.di.ParserGson
import org.koin.core.qualifier.StringQualifier
import org.koin.java.KoinJavaComponent.inject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import java.util.*

object WebSocketMessageFactory {

    private val gson: Gson by inject(
            Gson::class.java,
            StringQualifier(ParserGson)
    )

    fun createOrJoinGroup(groupId: String): String {
        return gson.toJson(
                SignalMessage.CreateOrJoinGroup(groupId)
        )
    }
}

sealed class SignalMessage(
        val id: String = UUID.randomUUID().toString(),
        val type: String,
        val from: String? = null,
        val to: String? = null,
        val groupId: String? = null,
        val candidate: IceCandidate? = null,
        val sdp: SessionDescription? = null
) {
    /**
     * 创建/加入群组
     */
    class CreateOrJoinGroup(groupId: String) : SignalMessage(
            type = WebSocketTypes.JoinGroup.type,
            groupId = groupId,
    )
}

//WebSocket 消息类型
sealed class WebSocketTypes(val type: String) {
    object JoinGroup : WebSocketTypes("create_join_group")
}
