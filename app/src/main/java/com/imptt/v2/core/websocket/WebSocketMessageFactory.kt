package com.imptt.v2.core.websocket

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.imptt.v2.data.model.UserInfo
import com.imptt.v2.di.ParserGson
import com.imptt.v2.utils.IUserSingleton
import org.koin.core.qualifier.StringQualifier
import org.koin.java.KoinJavaComponent.inject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import java.util.*

class WebSocketMessageFactory private constructor(private val user: UserInfo) {


    companion object : IUserSingleton<WebSocketMessageFactory>() {
        override fun createInstance(user: UserInfo): WebSocketMessageFactory {
            return WebSocketMessageFactory(user)
        }
    }

    private val gson: Gson by inject(
        Gson::class.java,
        StringQualifier(ParserGson)
    )

    fun createOrJoinGroup(groupId: String): SignalMessage.CreateOrJoinGroup {
        return SignalMessage.CreateOrJoinGroup(groupId, user.userId)
    }

    fun createRegister(): SignalMessage.RegisterToSignalServer {
        return SignalMessage.RegisterToSignalServer()
    }
}

data class Group @JvmOverloads constructor(
    val groupId: String? = null
):Parcelable {

    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(groupId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Group> {
        override fun createFromParcel(parcel: Parcel): Group {
            return Group(parcel)
        }

        override fun newArray(size: Int): Array<Group?> {
            return arrayOfNulls(size)
        }
    }
}

data class ImPttInfo(
    //用户id
    val id: String? = null,
    //用户群组
    val groups: List<Group>? = arrayListOf()
)

open class SignalMessage(
    val id: String = UUID.randomUUID().toString(),
    val type: String,
    val from: String? = null,
    val to: String? = null,
    val groupId: String? = null,
    val candidate: IceCandidate? = null,
    val sdp: SessionDescription? = null,
    val info: ImPttInfo? = null
) {

    /**
     * 失败消息
     */
    class Fail : SignalMessage(
        type = WebSocketTypes.Fail.type,
    )

    /**
     * 注册到信令服务
     */
    class RegisterToSignalServer : SignalMessage(
        type = WebSocketTypes.Register.type,
    )

    /**
     * 创建/加入群组
     */
    class CreateOrJoinGroup(groupId: String, userId: String) : SignalMessage(
        type = WebSocketTypes.JoinGroup.type,
        groupId = groupId,
        from = userId
    )
}

//WebSocket 消息类型
sealed class WebSocketTypes(val type: String) {
    object Fail : WebSocketTypes("fail")
    object Register : WebSocketTypes("register")
    object JoinGroup : WebSocketTypes("create_join_group")
    object Offer : WebSocketTypes("offer")
    object Answer : WebSocketTypes("answer")
    object Call : WebSocketTypes("call")
    object InCall : WebSocketTypes("in_call")
    object Candidate : WebSocketTypes("candidate")
}
