package com.imptt.v2.di

import com.google.gson.GsonBuilder
import com.imptt.v2.core.websocket.WebSocketConnection
import com.imptt.v2.data.ImDataBase
import com.imptt.v2.data.api.SignalServerApi
import com.imptt.v2.data.model.UserInfo
import com.imptt.v2.data.repo.ImRepository
import com.imptt.v2.vm.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 *  author : apm29[ciih]
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

    factory { (user: UserInfo) ->
        createWebSocketRequest(
            user
        )
    }


    //websocket不是单例，需要时再从okhttp新建相同实例
    factory {
        createWebSocket(
            get(named(WebSocketRelated)),
            get<WebSocketConnection>(),
            get()
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

fun createWebSocketRequest(userInfo: UserInfo): Request {
    return Request.Builder()
        .url("ws://192.168.10.181:8080/talk/websocket/v2/${userInfo.userId}")
        .build()
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
    .baseUrl("http://192.168.10.181:8080/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

fun createWebSocket(
    okHttpClient: OkHttpClient,
    listener: WebSocketListener,
    request: Request
): WebSocket {
    return okHttpClient.newWebSocket(request, listener)
}

fun createServerApi(
    retrofit: Retrofit
): SignalServerApi {
    return retrofit.create(SignalServerApi::class.java)
}


var viewModule = module {

    single {
        ImDataBase.getInstance(get())
    }

    single {
        get<ImDataBase>().getMessageDao()
    }

    single {
        get<ImDataBase>().getUserDao()
    }

    single {
        get<ImDataBase>().getGroupDao()
    }

    single {
        ImRepository(get(), get(), get())
    }

    viewModel {
        HomeViewModel(get())
    }

    viewModel {
        ContactViewModel()
    }

    viewModel {
        SettingsViewModel()
    }

    viewModel {
        GroupViewModel(get())
    }

    viewModel {
        GroupSettingsViewModel()
    }

    viewModel {
        GroupUsersViewModel()
    }

    viewModel {
        UserInfoViewModel()
    }

    viewModel {
        EditGroupViewModel(get())
    }
}