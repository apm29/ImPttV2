package com.imptt.v2.data.api

import com.imptt.v2.core.websocket.Group
import com.imptt.v2.data.model.UserInfo
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 *  author : ciih
 *  date : 2020/10/21 10:28 AM
 *  description :
 */
interface FileServiceApi {

    @POST("/talk/ws/file/upload")
    suspend fun upload(
        @Body image: MultipartBody
    ): BaseResp<String>

}