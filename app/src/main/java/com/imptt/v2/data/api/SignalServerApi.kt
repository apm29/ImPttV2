package com.imptt.v2.data.api

import com.imptt.v2.data.model.UserInfo
import retrofit2.http.Field
import retrofit2.http.POST

interface SignalServerApi {
    @POST
    suspend fun login(
            @Field(value = "user") userName: String
    ): UserInfo
}