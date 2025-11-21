package com.app.relatosfutbolerosnd.di

import android.content.Context
import com.app.relatosfutbolerosnd.data.repository.MatchRepositoryImpl
import com.app.relatosfutbolerosnd.data.repository.StreamingRepositoryImpl
import com.app.relatosfutbolerosnd.domain.repository.MatchRepository
import com.app.relatosfutbolerosnd.domain.repository.StreamingRepository
import com.app.relatosfutbolerosnd.service.RtmpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideStreamingRepository(
        @ApplicationContext context: Context
    ): StreamingRepository {
        return StreamingRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideMatchRepository(
        @ApplicationContext context: Context
    ): MatchRepository {
        return MatchRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideRtmpClient(
        @ApplicationContext context: Context
    ): RtmpClient {
        return RtmpClient(context)
    }
}