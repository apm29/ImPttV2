package com.imptt.v2.data.api

import com.imptt.v2.core.websocket.Group
import com.imptt.v2.data.model.UserInfo
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 *  author : ciih
 *  date : 2020/10/21 10:28 AM
 *  description :
 */
interface GroupServiceApi {

    @FormUrlEncoded
    @POST("/talk/ws/addGroup")
    suspend fun addGroup(
        @Field(value = "groupName") groupName: String,
        @Field(value = "groupIcon") groupIcon: String,
    ): BaseResp<Group>

}