package com.imptt.v2.data.api

import com.imptt.v2.data.entity.FileMessage
import com.imptt.v2.data.entity.Message
import com.imptt.v2.data.model.Page
import com.imptt.v2.data.model.UserInfo
import com.imptt.v2.data.model.v2.ChannelUser
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface SignalServerApi {
    @FormUrlEncoded
    @POST("/talk/ws/login")
    suspend fun login(
            @Field(value = "userId") userId: String
    ): BaseResp<UserInfo>


    @Multipart
    @POST("/talk/Mobile/Content/contentAdd")
    fun sendFileOrText(
        @Part(value = "t_uid") userId: RequestBody,
        @Part(value = "nick_name") nickname: RequestBody,
        @Part(value = "c_id") channelId: RequestBody,
        @Part(value = "content_type") contentType: RequestBody,//1:文本  2:文件
        @Part(value = "ext") extension: RequestBody,//1:文本  2:文件
        @Part(value = "content") content:RequestBody? = null,
        @Part(value = "upfile") file:RequestBody? = null,
        @Part(value = "file_name") fileName:RequestBody? = null,
        //@Body data:RequestBody
    ): Call<BaseResp<Any>>

    @Multipart
    @POST("/talk/Mobile/Content/contentAdd")
    suspend fun sendFileOrTextAync(
        @Part(value = "t_uid") userId: RequestBody,
        @Part(value = "nick_name") nickname: RequestBody,
        @Part(value = "c_id") channelId: RequestBody,
        @Part(value = "content_type") contentType: RequestBody,//1:文本  2:文件
        @Part(value = "ext") extension: RequestBody?= null,
        @Part(value = "content") content:RequestBody? = null,
        @Part(value = "upfile") file:RequestBody? = null,
        @Part(value = "file_name") fileName:RequestBody? = null,
        //@Body data:RequestBody
    ): BaseResp<Any>

    @FormUrlEncoded
    @POST("/talk/Mobile/Content/historySearchPage")
     fun getHistoryFileMessages(
        @Field(value = "c_id") channelId: Int,
        @Field(value = "date") date: Long,
        @Field(value = "page") page: Int = 1,
        @Field(value = "pagenum") pageNo: Int = 99999,
    ): Call<BaseResp<Page<FileMessage>>>

    @FormUrlEncoded
    @POST("/talk/Mobile/Content/historySearchPage")
    suspend fun getHistoryFileMessagesAsync(
        @Field(value = "c_id") channelId: Int,
        @Field(value = "date") date: Long,
        @Field(value = "page") page: Int = 1,
        @Field(value = "pagenum") pageNo: Int = 99999,
    ): BaseResp<Page<FileMessage>>


    //用获取当前频道所有用户列表
    @FormUrlEncoded
    @POST("/talk/Mobile/User/getChannelList")
    suspend fun getAllUserInChannel(
        @Field(value = "c_id") channelId: Int
    ):BaseResp<List<ChannelUser>>

    //用获取当前频道可添加用户列表
    @FormUrlEncoded
    @POST("/talk/Mobile/User/getCanAddUserList")
    suspend fun getAllUserNotInChannel(
        @Field(value = "c_id") channelId: Int
    ):BaseResp<List<ChannelUser>>


    //往频道中添加用户,用户id  多个id用,隔开
    @FormUrlEncoded
    @POST("/talk/Mobile/User/userChannelAdd")
    suspend fun addUserToChannel(
        @Field(value = "c_id")channelId: Int,
        @Field(value = "t_uid")userId: String
    ): BaseResp<Any>
}