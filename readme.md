# 项目说明

## 基本架构
1. service：ImService 运行在单独的:ptt 进程中，负责连接、接收数据（WebRTC的信令、P2P流）
2. view:各种Activity、Fragment，运行在应用进程，负责展示数据
3. service 与 view 的通信：通过Messenger进行 详细说明：(Messenger)[https://developer.android.google.cn/guide/components/bound-services#Messenger]
    * Messenger传递的参数需要是Parcelable的
    * 通过为Message添加replyTo来传递Messenger对象到另一进程，可参考Android Developer的模式将主进程的Messenger`注册`到ptt进程，ptt可在需要时通知（发送Message）主进程
    * 定义好Message.what(放在core.messenger.connections.Connections.kt下)

4.Service与信令服务器通信
    * 通过SignalServiceConnector ,包含WebSocket连接:WebSocket WebRtc连接管理:WebRtcConnector
    * 使用WebSocket交换ICE,SDP等信息,WebRtcConnector呼叫方每个group包含N-1个Peer,应答方每个group保有一个Peer
    * WebSocket通过WebSocketMessageFactory生成SignalMessage发送给信令服务器

## DI
 使用 (Hilt)[https://developer.android.google.cn/training/dependency-injection/hilt-android#hilt-modules]
 使用 (Koin)[https://insert-koin.io/]
 下面是koin例子
 ```kotlin
 package com.imptt.v2.di

 import com.google.gson.GsonBuilder
 import com.imptt.v2.core.websocket.WebSocketConnection
 import com.imptt.v2.data.api.SignalServerApi
 import com.imptt.v2.data.model.UserInfo
 import okhttp3.OkHttpClient
 import okhttp3.Request
 import okhttp3.WebSocket
 import okhttp3.WebSocketListener
 import okhttp3.logging.HttpLoggingInterceptor
 import org.koin.core.qualifier.named
 import org.koin.dsl.module
 import retrofit2.Retrofit
 import retrofit2.converter.gson.GsonConverterFactory
 import java.util.concurrent.TimeUnit

 const val WebSocketRelated = "websocket-okhttpclient"
 const val HttpApiRelated = "http-okhttpclient"
 const val PrettyPrintGson = "pretty_print"
 const val ParserGson = "parser"

 var serviceModule = module {
     single(named(WebSocketRelated)) {
         provideWebSocketOkHttpClient()
     }

     single {
         provideWebSocketRetrofit(get(named(WebSocketRelated)))
     }

     single(named(PrettyPrintGson)) {
         GsonBuilder().setPrettyPrinting().create()
     }
     single(named(ParserGson)) {
         GsonBuilder().setPrettyPrinting().create()
     }

     //websocket listener
     single {
         WebSocketConnection()
     }

     //websocket不是单例，需要时再从okhttp新建相同实例
     factory { (user: UserInfo) ->
         createWebSocket(
             get(named(WebSocketRelated)),
             user,
             get<WebSocketConnection>()
         )
     }


     //api
     single(named(HttpApiRelated)) {
         provideHttpOkHttpClient()
     }
     single(named(HttpApiRelated)) {
         provideHttpRetrofit(
             get(named(HttpApiRelated))
         )
     }
     single {
         createServerApi(
             get(named(HttpApiRelated))
         )
     }
 }

 fun provideWebSocketOkHttpClient() = OkHttpClient.Builder()
     .pingInterval(30000L, TimeUnit.MILLISECONDS)
     .connectTimeout(300000L, TimeUnit.MILLISECONDS)
     .readTimeout(300000L, TimeUnit.MILLISECONDS)
     .build()

 fun provideHttpOkHttpClient() = OkHttpClient.Builder()
     .connectTimeout(150000L, TimeUnit.MILLISECONDS)
     .readTimeout(150000L, TimeUnit.MILLISECONDS)
     .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
     .build()

 fun provideWebSocketRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
     .client(okHttpClient)
     .addConverterFactory(GsonConverterFactory.create())
     .build()

 fun provideHttpRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
     .client(okHttpClient)
 //    .baseUrl("http://192.168.10.102:8080/")
     .baseUrl("http://172.20.10.5:8080/")
     .addConverterFactory(GsonConverterFactory.create())
     .build()

 fun createWebSocket(
     okHttpClient: OkHttpClient,
     userInfo: UserInfo,
     listener: WebSocketListener
 ): WebSocket {
     val request = Request.Builder()
 //        .url("ws://192.168.10.102:8080/talk/websocket/v2/${userInfo.userId}")
         .url("ws://172.20.10.5:8080/talk/websocket/v2/${userInfo.userId}")
         .build()
     return okHttpClient.newWebSocket(request, listener)
 }

 fun createServerApi(
     retrofit: Retrofit
 ): SignalServerApi {
     return retrofit.create(SignalServerApi::class.java)
 }
 ```

 注入处使用
 ```kotlin
     private val mWebSocketConnection: WebSocketConnection by inject(
         WebSocketConnection::class.java
     )

     private val mWebRtcConnector: WebRtcConnector = WebRtcConnector(context, userInfo, this)

     private val mWebSocket: WebSocket by inject(
         WebSocket::class.java,
     ) {
         parametersOf(userInfo)
     }
 ```
