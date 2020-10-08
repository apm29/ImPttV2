package com.imptt.v2.core.messenger.connections

import android.os.Message

//Headers:定义view发送到service的消息类型
const val MESSAGE_TYPE_UNREGISTER_VIEW = 2009
const val MESSAGE_TYPE_REGISTER_VIEW = 2010
const val MESSAGE_TYPE_SEND_WEB_SOCKET = 2011
const val MESSAGE_TYPE_START_P2P = 2012
const val MESSAGE_TYPE_CHANGE_NOTIFICATION_TEXT = 2013
const val MESSAGE_TYPE_CHANGE_NOTIFICATION_TITLE = 2014
const val MESSAGE_TYPE_ECHO_TEST = 2015


typealias ServiceMessageCallback = (message: Message) -> Unit
typealias ViewMessageCallback = (message: Message) -> Unit