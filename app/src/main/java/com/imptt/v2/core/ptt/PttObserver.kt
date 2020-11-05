package com.imptt.v2.core.ptt

import android.bluetooth.BluetoothDevice
import com.google.gson.Gson
import com.kylindev.pttlib.db.ChatMessageBean
import com.kylindev.pttlib.service.BaseServiceObserver
import com.kylindev.pttlib.service.InterpttService
import com.kylindev.pttlib.service.model.Channel
import com.kylindev.pttlib.service.model.Contact
import com.kylindev.pttlib.service.model.User
import com.kylindev.pttlib.utils.ServerProto

/**
 *  author : ciih
 *  date : 2020/10/22 7:32 PM
 *  description :
 */
open class PttObserver(
    private val tag:String? = "PttObserver"
) :BaseServiceObserver() {
    override fun onChannelAdded(channel: Channel) {
        super.onChannelAdded(channel)
        println("$tag.onChannelAdded")
        println("$tag:channel = [${channel}]")
    }

    override fun onAmrData(data: ByteArray?, length: Int, duration: Int) {
        super.onAmrData(data, length, duration)
        println("$tag.onAmrData")
    }

    override fun onChannelRemoved(channel: Channel) {
        super.onChannelRemoved(channel)
        println("$tag.onChannelRemoved")
        println("$tag:channel = [${channel}]")
    }

    override fun onChannelUpdated(channel: Channel) {
        super.onChannelUpdated(channel)
        println("$tag.onChannelUpdated")
    }

    override fun onConnectionStateChanged(state: InterpttService.ConnState) {
        super.onConnectionStateChanged(state)
        println("$tag.onConnectionStateChanged")
    }

    override fun onCurrentChannelChanged() {
        super.onCurrentChannelChanged()
        println("$tag.onCurrentChannelChanged")
    }

    override fun onCurrentUserUpdated() {
        super.onCurrentUserUpdated()
        println("$tag.onCurrentUserUpdated")
    }

    override fun onUserUpdated(user: User?) {
        super.onUserUpdated(user)
        println("$tag.onUserUpdated")
    }

    override fun onUserTalkingChanged(user: User?, talking: Boolean) {
        super.onUserTalkingChanged(user, talking)
        println("$tag.onUserTalkingChanged")
    }

    override fun onUserOrderCall(user: User?, p1: Boolean, p2: String) {
        super.onUserOrderCall(user, p1, p2)
        println("$tag.onUserOrderCall")
    }

    override fun onLocalUserTalkingChanged(user: User?, talking: Boolean) {
        super.onLocalUserTalkingChanged(user, talking)
        println("$tag.onLocalUserTalkingChanged user = [${user}], talking = [${talking}]")
    }

    override fun onNewVolumeData(volume: Short) {
        super.onNewVolumeData(volume)
    }

    override fun onPermissionDenied(reason: String, code: Int) {
        super.onPermissionDenied(reason, code)
        println("$tag.onPermissionDenied")
        println("$tag:reason = [${reason}], code = [${code}]")
    }

    override fun onRegisterResult(p0: Int) {
        super.onRegisterResult(p0)
        println("$tag.onRegisterResult")
    }

    override fun onForgetPasswordResult(p0: Boolean) {
        super.onForgetPasswordResult(p0)
        println("$tag.onForgetPasswordResult")
    }

    override fun onRejected(rejectType: ServerProto.Reject.RejectType) {
        super.onRejected(rejectType)
        println("$tag.onRejected")
    }

    override fun onMicStateChanged(p0: InterpttService.MicState) {
        super.onMicStateChanged(p0)
        println("$tag.onMicStateChanged")
    }

    override fun onHeadsetStateChanged(headState: InterpttService.HeadsetState) {
        super.onHeadsetStateChanged(headState)
        println("$tag.onHeadsetStateChanged")
        println("$tag:headState = [${headState}]")
    }

    override fun onScoStateChanged(p0: Int) {
        super.onScoStateChanged(p0)
        println("$tag.onScoStateChanged")
    }

    override fun onTargetHandmicStateChanged(
        p0: BluetoothDevice,
        p1: InterpttService.HandmicState
    ) {
        super.onTargetHandmicStateChanged(p0, p1)
        println("$tag.onTargetHandmicStateChanged")
    }

    override fun onLeDeviceScanStarted(p0: Boolean) {
        super.onLeDeviceScanStarted(p0)
        println("$tag.onLeDeviceScanStarted")
    }

    override fun onLeDeviceFound(device: BluetoothDevice) {
        super.onLeDeviceFound(device)
        println("$tag.onLeDeviceFound")
    }

    override fun onTalkingTimerTick(duration: Int) {
        super.onTalkingTimerTick(duration)
        println("$tag.onTalkingTimerTick")
        println("$tag:duration = [${duration}]")
    }

    override fun onTalkingTimerCanceled() {
        super.onTalkingTimerCanceled()
        println("$tag.onTalkingTimerCanceled")
    }

    override fun onUserAdded(p0: User?) {
        super.onUserAdded(p0)
        println("$tag.onUserAdded")
    }

    override fun onUserRemoved(p0: User?) {
        super.onUserRemoved(p0)
        println("$tag.onUserRemoved")
    }

    override fun onChannelSearched(
        p0: Int,
        p1: String,
        p2: Boolean,
        p3: Boolean,
        p4: Int,
        p5: Int
    ) {
        super.onChannelSearched(p0, p1, p2, p3, p4, p5)
        println("$tag.onChannelSearched")
        println("$tag:p0 = [${p0}], p1 = [${p1}], p2 = [${p2}], p3 = [${p3}], p4 = [${p4}], p5 = [${p5}]")
    }

    override fun onShowToast(message: String) {
        super.onShowToast(message)
        println("$tag.onShowToast")
    }

    override fun onPlaybackChanged(channelId: Int, resId: Int, play: Boolean) {
        super.onPlaybackChanged(channelId, resId, play)
        println("$tag.onPlaybackChanged channelId = [${channelId}], resId = [${resId}], play = [${play}]")
    }

    override fun onRecordFinished(messageBean: ChatMessageBean) {
        super.onRecordFinished(messageBean)
        println("$tag.onRecordFinished ${Gson().toJson(messageBean)}")
    }

    /**
     * 其中sendPcmRecord是short[]数组，pcmRecordLength是数组长度
     */
    override fun onPcmRecordFinished(sendPcmRecord: ShortArray, pcmRecordLength: Int) {
        super.onPcmRecordFinished(sendPcmRecord, pcmRecordLength)
        println("$tag.onPcmRecordFinished")
    }

    override fun onInvited(p0: Channel) {
        super.onInvited(p0)
        println("$tag.onInvited")
    }

    override fun onUserSearched(p0: User?) {
        super.onUserSearched(p0)
        println("$tag.onUserSearched")
    }

    override fun onApplyContactReceived(p0: Boolean, p1: Contact) {
        super.onApplyContactReceived(p0, p1)
        println("$tag.onApplyContactReceived")
    }

    override fun onPendingContactChanged() {
        super.onPendingContactChanged()
        println("$tag.onPendingContactChanged")
    }

    override fun onContactChanged() {
        super.onContactChanged()
        println("$tag.onContactChanged")
    }

    override fun onPendingMemberChanged() {
        super.onPendingMemberChanged()
        println("$tag.onPendingMemberChanged")
    }

    override fun onSynced() {
        super.onSynced()
        println("$tag.onSynced")
    }

    override fun onVoiceToggleChanged(on: Boolean) {
        super.onVoiceToggleChanged(on)
        println("$tag.onVoiceToggleChanged on = [$on]")
    }

    override fun onMembersGot(p0: Int, p1: String) {
        super.onMembersGot(p0, p1)
        println("$tag.onMembersGot")
    }

    override fun onListenChanged(listen: Boolean) {
        super.onListenChanged(listen)
        println("$tag.onListenChanged")
    }

    override fun onApplyOrderResult(p0: Int, p1: Int, p2: String, p3: Boolean) {
        super.onApplyOrderResult(p0, p1, p2, p3)
        println("$tag.onApplyOrderResult")
    }

    override fun onGeneralMessageGot(p0: Int, p1: Int, p2: Int, p3: Int, p4: String) {
        super.onGeneralMessageGot(p0, p1, p2, p3, p4)
        println("$tag.onGeneralMessageGot")
    }

    override fun onBleButtonDown(down: Boolean) {
        super.onBleButtonDown(down)
        println("$tag.onBleButtonDown")
    }
}