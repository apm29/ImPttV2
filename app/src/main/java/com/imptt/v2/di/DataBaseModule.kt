package com.imptt.v2.di

import android.content.Context
import com.imptt.v2.data.ImDataBase
import com.imptt.v2.data.dao.MessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class DataBaseModule {
    @Singleton
    @Provides
    fun provideImDataBase(@ApplicationContext context: Context):ImDataBase{
        return ImDataBase.getInstance(context)
    }

    @Provides
    fun provideMessageDao(imDataBase: ImDataBase):MessageDao{
        return imDataBase.getMessageDao()
    }
}