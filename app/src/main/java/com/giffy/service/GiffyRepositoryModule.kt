package com.giffy.service

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GiffyRepositoryModule {

    @Singleton
    @Provides
    fun provideGiffyRepository(
        repo: GiffyRepositoryImpl
    ): GiffyRepository = repo
}
