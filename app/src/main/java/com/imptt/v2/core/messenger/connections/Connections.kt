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
//ws register成功后返回group列表，发送到View
const val MESSAGE_TYPE_GROUP_LIST = 2016
const val MESSAGE_DATA_KEY_GROUP_LIST = "group" //data key 群组列表
//ws 收到呼叫
const val MESSAGE_TYPE_IN_CALL = 2017
const val MESSAGE_DATA_KEY_GROUP_ID = "group_id"
const val MESSAGE_DATA_KEY_MESSAGE = "message"
const val MESSAGE_DATA_KEY_FROM_USER_ID = "from_user_id"
// view 呼叫
const val MESSAGE_TYPE_CALL = 2018
const val MESSAGE_TYPE_END_CALL = 2019
const val MESSAGE_TYPE_MESSAGE = 2020
const val MESSAGE_TYPE_GET_GROUPS_INFO = 2021
const val MESSAGE_TYPE_USER_LOGIN = 2022

typealias ServiceMessageCallback = (message: Message) -> Unit
typealias ViewMessageCallback = (message: Message) -> Unit