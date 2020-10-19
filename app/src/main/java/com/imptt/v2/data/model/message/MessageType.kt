package com.imptt.v2.data.model.message

enum class MessageType(val type: Int) {
    TEXT_OTHER(10001),
    TEXT_ME(10002),
    AUDIO_OTHER(10003),
    AUDIO_ME(10004),
    VIDEO_OTHER(10005),
    VIDEO_ME(10006),
    IMAGE_OTHER(10007),
    IMAGE_ME(10008),
    FILE_OTHER(10009),
    FILE_ME(10010),
}