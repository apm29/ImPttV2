package com.imptt.v2.di

import com.google.gson.GsonBuilder
import com.imptt.v2.core.websocket.SignalServerConnection
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

/**
 *  author : ciih
 *  date : 2020/9/30 10:47 AM
 *  description :
 */
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

    single { (user: UserInfo) ->
        createWebSocket(
            get(named(WebSocketRelated)),
            user,
            get<SignalServerConnection>()
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
        .addInterceptor( HttpLoggingInterceptor())
        .build()

fun provideWebSocketRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

fun provideHttpRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
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

fun createServerApi(
        retrofit: Retrofit
):SignalServerApi{
    return retrofit.create(SignalServerApi::class.java)
}