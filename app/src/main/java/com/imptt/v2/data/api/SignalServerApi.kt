package com.imptt.v2.data.api

import com.imptt.v2.data.model.UserInfo
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SignalServerApi {
    @FormUrlEncoded
    @POST("/talk/ws/login")
    suspend fun login(
            @Field(value = "userId") userId: String
    ): BaseResp<UserInfo>
}