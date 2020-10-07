package com.imptt.v2.di

import com.google.gson.GsonBuilder
import com.imptt.v2.core.websocket.SignalServerConnection
import com.imptt.v2.data.model.UserInfo
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 *  author : ciih
 *  date : 2020/9/30 10:47 AM
 *  description :
 */
const val WebSocketOkHttp = "websocket-okhttpclient"
const val PrettyPrintGson = "pretty_print"
const val ParserGson = "parser"

var serviceModule = module {
    single(named(WebSocketOkHttp)) {
        provideOkHttpClient()
    }

    single {
        provideRetrofit(get(named(WebSocketOkHttp)))
    }

    single(named(PrettyPrintGson)){
        GsonBuilder().setPrettyPrinting().create()
    }
    single(named(ParserGson)){
        GsonBuilder().create()
    }

    //websocket listener
    single {
        SignalServerConnection()
    }

    single<WebSocket> { (user: UserInfo) ->
        createWebSocket(
            get(named(WebSocketOkHttp)),
            user,
            get<SignalServerConnection>()
        )
    }
}

fun provideOkHttpClient() = OkHttpClient.Builder()
    .pingInterval(30000L, TimeUnit.MILLISECONDS)
    .connectTimeout(300000L, TimeUnit.MILLISECONDS)
    .readTimeout(300000L, TimeUnit.MILLISECONDS)
    .build()

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

fun createWebSocket(
    okHttpClient: OkHttpClient,
    userInfo: UserInfo,
    listener: WebSocketListener
): WebSocket {
    val request = Request.Builder()
        .url("ws://192.168.10.185:8080/talk/websocket/${userInfo.userId}")
        .build()
    return okHttpClient.newWebSocket(request, listener)
}